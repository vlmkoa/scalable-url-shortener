import { useNavigate } from "react-router-dom";

function NotFound() {
    const navigate = useNavigate();

    return (
        <div className="d-flex align-items-center justify-content-center min-vh-100 bg-light">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-12 col-md-8 col-lg-6 text-center">
                        <h1 className="display-1 fw-bold text-primary">404</h1>
                        <h2 className="mb-3">Oops! Link not found.</h2>
                        <p className="lead text-muted mb-4">
                            The short link you are looking for doesn't exist or has been removed.
                        </p>
                        <button
                            className="btn btn-primary btn-lg px-5 shadow-sm"
                            onClick={() => navigate('/')}
                        >
                            Back to Home
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default NotFound;