var PetView = (function(){
    var dao;

    var self;
    var speciesList = ['DOG','CAT','BIRD','RABBIT','OTHER'];

    var mainContainerId;

    var formId = 'pet-form';
    var listAllPetsId = 'pet-list';
    var formQuery = '#' + formId;
    var listAllPetsQuery = '#' + listAllPetsId;

    var formPetOwnerId = 'pet-owner-form';
    var listPetsOwnerId = 'owner-pet-list';
    var listPetsOwnerQuery = '#' + listPetsOwnerId;
    var formPetOwnerQuery = '#' + formPetOwnerId;

    function PetView(petDao, containerId) {
        dao = petDao;
        self = this;
        mainContainerId = containerId;

        this.init = function() {
            //Create header and view toggle button
            insertToggleViewHeader($('#' + mainContainerId));
            addToggleViewListener();

            /*View 1: (Default on charge)
                Create and insert: pet form and all pets list
            */
            insertPetForm($('#' + mainContainerId));
            insertPetList($('#' + mainContainerId));
            /*View 2:
                Create and insert : list by owner form and owner pets list
            */
            insertOwnerPetsForm($('#' + mainContainerId));
            insertOwnerPetsList($('#' + mainContainerId));

            //Ocultar al cargar pagina listar por id owner
            //$('#' + mainContainerId).hide();
            $(formPetOwnerQuery).hide();
            $(listPetsOwnerQuery).hide();

            dao.listPets(function(pets){
                $.each(pets, function(key, pet) {
                    appendToTable(pet);
                });
            },
            function() {
                alert('No ha sido posible acceder al listado de mascotas.');
            });

            $(formPetOwnerQuery).submit(function() {
                var owner_id = self.getOwnerId();
                //Limpiar tbody antes de agregar mascotas de nuevo dueño
                $(listPetsOwnerQuery + ' tbody').empty();

                dao.listByOwner(owner_id,
                    function(pets) {
                        $.each(pets, function(key, pet) {
                            appendToOwnerTable(pet);
                            self.resetForm(formPetOwnerQuery);
                        });
                    },
                    function() {
                        alert('No ha sido posible acceder al listado de mascotas del propietario');
                    }
                );

                return false;
            });

            $(formQuery).submit(function(event) {
                var pet = self.getPetInForm();
                
                if (self.isEditing()) {
                    dao.modifyPet(pet,
                        function(pet) {
                            $('#pet-' + pet.id + ' td.name').text(pet.name);
                            $('#pet-' + pet.id + ' td.species').text(pet.species);
                            $('#pet-' + pet.id + ' td.breed').text(pet.breed);
                            $('#pet-' + pet.id + ' td.ownerId').text(pet.ownerId);
                            self.resetForm(formQuery);
                        },
                        showErrorMessage,
                        self.enableForm
                    );
                } else {
                    dao.addPet(pet,
                        function(pet) {
                            appendToTable(pet);
                            self.resetForm(formQuery);
                        },
                        showErrorMessage,
                        self.enableForm
                    );
                }

                return false;
            });

            $('btnClear').click(this.resetForm);
        };

        this.getOwnerId = function() {
            var input = $(formPetOwnerQuery);
            return input.find('input[name="owner_id"]').val();
        };

        this.getPetInForm = function() {
            var form = $(formQuery);
            return {
                'id' : form.find('input[name="id"]').val(),
                'name' : form.find('input[name="name"]').val(),
                'specie' : form.find('select[name="specie-select"]').val(),
                'breed' : form.find('input[name="breed"]').val(),
                'owner_id' : form.find('input[name="owner_id"]').val()
            }
        };

        this.getPetInRow = function(id) {
            var row = $('#pet-' + id);

            if (row !== undefined) {
                return {
                    'id' : id,
                    'name' : row.find('td.name').text(),
                    'species' : row.find('td.specie').text(),
                    'breed' : row.find('td.breed').text(),
                    'ownerId' : row.find('td.owner_id').text()
                };
            } else {
                return undefined;
            }
        };

        this.editPet = function(id) {
            var row = $('#pet-' + id);

            if (row !== undefined) {
                var form = $(formQuery);

                form.find('input[name="id"]').val(id);
                form.find('input[name="name"]').val(row.find('td.name').text());                            
                form.find('select[name="specie-select"]').val(row.find('td.species').text());
                form.find('input[name="breed"]').val(row.find('td.breed').text());
                form.find('input[name="owner_id"]').val(row.find('td.ownerId').text());

                $('input#btnSubmit').val('Modificar');
            }
        };

        this.deletePet = function(id) {
            if (confirm('Esta a punto de eliminar a una mascota. ¿Está seguro de que desea continuar?')) {
                dao.deletePet(id,
                    function() {
                        $('tr#pet-' + id).remove();
                    },
                    showErrorMessage
                );
            }
        };

        this.isEditing = function() {
            return $(formQuery + ' input[name="id"]').val() != "";
        };

        this.enableForm = function() {
            $(formQuery + ' input').prop('disabled', false);
        };

        this.disableForm = function() {
            $(formQuery + ' input').prop('disabled', true);
        };

        this.resetForm = function(query) {
            $(query)[0].reset();
            $(query + ' input[name="id"]').val('');
            $('#btnSubmit').val('Crear');   
        };

        this.ToggleView = function() {
            //Show or hide view 1
            $(formQuery).toggle();
            $(listAllPetsQuery).toggle();
            //Show or hide view 2
            $(formPetOwnerQuery).toggle();
            $(listPetsOwnerQuery).toggle();
            //Change title
            titleTxt = $('#title-view').text(); 
            $('#title-view').text($('#toggle-pet-view-btn').text());
            $('#toggle-pet-view-btn').text(titleTxt);
        };
    };

    var insertOwnerPetsForm = function(parent) {
        parent.append(
            '<form id="' + formPetOwnerId + '" class="mb-5 mb-10">\
                <div class="row">\
                    <div class="col-sm-3">\
                        <input name="owner_id" type="number" value="" placeholder="Id Dueño" class="form-control" required/>\
                    </div>\
                    <div class="col-sm-3">\
                        <input id="btnList" type="submit" value="Listar" class="btn btn-primary"/>\
                    </div>\
                    <div class="col-sm-3">\
                        <input id="btnClear" type="reset" value="Limpiar" class="btn btn-secondary"/>\
                    <div>\
                </div>\
            </form>'
        );
    };

    var insertPetForm = function(parent) {
        specieSelectId = 'specie-select';

        parent.append(
            '<form id="' + formId + '" class="mb-5 mb-10">\
            <input name="id" type="hidden" value=""/>\
            <div class="row">\
                <div class="col-sm-3">\
                    <input name="name" type="text" value="" placeholder="Nombre" class="form-control" required/>\
                </div>\
                <div class="col-sm-3">\
                    <select name="specie-select" id="' + specieSelectId + '">\
                            <option selected disabled>-Select Specie-</option>\
                    </select>\
                </div>\
                <div class="col-sm-2">\
                    <input name="breed" type="text" value="" placeholder="Raza" class="form-control"/>\
                </div>\
                <div class="col-sm-2">\
                    <input name="owner_id" type="number" value="" placeholder="Id Dueño" class="form-control" required/>\
                </div>\
                <div class="col-sm-2">\
                    <input id="btnSubmit" type="submit" value="Crear" class="btn btn-primary"/>\
                    <input id="btnClear" type="reset" value="Limpiar" class="btn btn-secondary"/>\
                <div>\
            </div>\
            </form>'
        );

        var select = $('#' + specieSelectId);
        speciesList.forEach(function(specie) {
            select.append('<option value"' + specie + '">' + specie + '</option>');
        });
    };

    var insertPetList = function(parent) {
        parent.append(
            '<table id="' + listAllPetsId + '" class="table">\
                <thead>\
                    <tr class="row">\
                        <th class="col-sm-3">Nombre</th>\
                        <th class="col-sm-2">Especie</th>\
                        <th class="col-sm-3">Raza</th>\
                        <th class="col-sm-2">Id Dueño</th>\
                        <th class="col-sm-2">&nbsp;</th>\
                    </tr>\
                </thead>\
                <tbody>\
                </tbody>\
            </table>'
        );
    };

    var insertOwnerPetsList = function(parent) {
        parent.append(
            '<table id="' + listPetsOwnerId +'" class="table">\
            <thead>\
                <tr class="row">\
                    <th class="col-sm-4">Nombre</th>\
                    <th class="col-sm-2">Especie</th>\
                    <th class="col-sm-4">Raza</th>\
                    <th class="col-sm-2">&nbsp;</th>\
                </tr>\
            </thead>\
            <tbody>\
            </tbody>\
            </table>'
        );
    };

    var insertToggleViewHeader = function(parent) {
        parent.prepend(
            '<div class="d-flex justify-content-between align-items-center">\
                <h1 id="title-view" class="display-5 mt-3 mb-3">Mascotas</h1>\
                <a id="toggle-pet-view-btn" class="btn btn-primary" href="#">Listar por Dueño</a>\
            </div>'
        );
    };

    var addToggleViewListener = function() {
        $('#toggle-pet-view-btn').click(function() {
            self.ToggleView();

        });
    }

    var appendToTable = function(pet) {
        $(listAllPetsQuery + ' > tbody:last').append(createPetRow(pet));
        addRowListeners(pet);
    };

    var appendToOwnerTable = function(pet) {
        $(listPetsOwnerQuery + ' > tbody:last').append(createPetRow(pet));
        addRowListeners(pet);
    };

    var createPetRow = function(pet) {        
        return '<tr id="pet-' + pet.id +'" class="row">\
            <td class="name col-sm-3">' + pet.name + '</td>\
            <td class="species col-sm-2">' + pet.species + '</td>\
            <td class="breed col-sm-3">' + pet.breed + '</td>\
            <td class="ownerId col-sm-2">' + pet.ownerId + '</td>\
            <td class="col-sm-2">\
                <a class="edit btn btn-primary" href="#">Editar</a>\
                <a class="delete btn btn-warning" href="#">Eliminar</a>\
            </td>\
            </tr>';
    };

    var addRowListeners = function(pet) {
        $('#pet-' + pet.id + ' a.edit').click(function() {
            self.editPet(pet.id);
        });

        $('#pet-' + pet.id + ' a.delete').click(function() {
            self.deletePet(pet.id);
        });
    };

    var showErrorMessage = function(jqxhr, textStatus, error) {
        alert(textStatus + ": " + error);
    };

    return PetView;
})();

