
export default function AuthSideImg({ imgSrc, altText }) {

    return (
    <div className="col-md-5 p-0 rounded-bottom-3 overflow-hidden">
        
        <img
            src={imgSrc}
            alt={altText}
            className="w-100 h-100 object-fit-cover"
        />
    
    </div>
    );
}