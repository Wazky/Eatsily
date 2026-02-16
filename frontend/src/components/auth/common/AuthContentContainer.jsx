
export default function AuthContentContainer({ children }) {

    return (
        <div className="col-md-6 ms-5 mt-3 d-flex flex-column">
            <div className="my-auto">
                {children}
            </div>
        </div>
    );
}