var ViewController = (function() {

    var mainContainerId;
    var toggleButtonId;

    var currentView;
    var peopleView;
    var petView;
    var petsByOwnerView;

    function ViewController(containerId, buttonId) {
        //Guardamos los identificadores de los contenedores y botones
        mainContainerId = containerId;
        toggleButtonId = buttonId;

        this.init =  function() {
            //Creamos las vistas
            peopleView = new PeopleView(new PeopleDAO(), mainContainerId, this);
            petView = new PetView(new PetDAO(), mainContainerId);
            petsByOwnerView = new OwnerPetsView(new PetDAO(), mainContainerId);
            currentView = 'people';
            //Mostramos la vista de personas
            peopleView.init();
            //petView.init();
        };

        /**
         * Cambia la vista actual entre personas
         * y mascotas.
         */
        this.toggleView = function() {
            $('#' + mainContainerId).empty();
            if (currentView === 'people') {
                petView.init();
                currentView = 'pets';
                $('#' + toggleButtonId).text('Ver personas');
            } else {
                peopleView.init();
                currentView = 'people';
                $('#' + toggleButtonId).text('Ver mascotas');
            }
        };

        this.petsByOwnerView = function(owner_id, owner_fullname) {
            $('#' + mainContainerId).empty();
            petsByOwnerView.init(owner_id, owner_fullname);
        };
    }

    return ViewController;
})();