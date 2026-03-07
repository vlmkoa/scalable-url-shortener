import { useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import { useAuth } from './context/AuthContext'

function Home() {
    const { user, logout } = useAuth()
    const [originalUrl, setOriginalUrl] = useState('')
    const [customAlias, setCustomAlias] = useState('')
    const [shortUrl, setShortUrl] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)
    const [aliasError, setAliasError] = useState(""); // New state for live validation

    const handleAliasChange = (e) => {
        const value = e.target.value;
        if (value.includes(" ")) {
            setAliasError("Spaces are not allowed");
        }
        else if (value && !/^[a-zA-Z0-9-_]*$/.test(value)) {
            setAliasError("Only letters, numbers, '-' and '_' allowed");
        }
        else {
            setAliasError("");
        }

        setCustomAlias(value);
    };

    const copyToClipboard = () => {
        navigator.clipboard.writeText(shortUrl);
        alert("Copied to clipboard");
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError('')
        setShortUrl('')
        setLoading(true)

        try {
            const headers = user?.token ? { Authorization: `Bearer ${user.token}` } : undefined;
            const response = await axios.post(
                '/api/shorten',
                {
                    originalUrl: originalUrl,
                    customAlias: customAlias
                },
                { headers }
            )
            const shortenUrl = `${window.location.origin}/${response.data}`
            setShortUrl(shortenUrl)

            } catch (err) {
                let msg = "Something went wrong";
                    if (err.response && err.response.data) {
                        const data = err.response.data;
                        if (typeof data === "string") {
                            msg = data;
                        }
                        else if (data.message) {
                            msg = data.message;
                        }
                    }
                    else if (err.request) {
                        msg = "Network Error: Server unreachable";
                    }
                    setError(msg);
            } finally {
                setLoading(false)
            }
    }

    return (
        <div className="container-fluid min-vh-100 d-flex align-items-center justify-content-center bg-light">

            <div className="card shadow-lg" style={{ maxWidth: '500px', width: '100%' }}>
                <div className="card-body text-center">
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h1 className="card-title mb-0">🔗 URL Shortener</h1>
                        {user ? (
                            <div className="d-flex align-items-center gap-2">
                                {user.role === 'ADMIN' && (
                                    <Link to="/admin" className="btn btn-outline-dark btn-sm">Admin</Link>
                                )}
                                <span className="text-muted small">{user.email}</span>
                                <button type="button" className="btn btn-outline-secondary btn-sm" onClick={logout}>Log out</button>
                            </div>
                        ) : (
                            <Link to="/login" className="btn btn-outline-primary btn-sm">Log in</Link>
                        )}
                    </div>
                    <p className="card-text text-muted mb-2">
                        Enter a link and we will shorten it for you!
                    </p>
                    {!user && (
                        <p className="mb-4">
                            <Link to="/login" className="btn btn-primary btn-sm">Log in</Link>
                            <span className="text-muted small ms-2">or</span>
                            <Link to="/register" className="btn btn-outline-primary btn-sm ms-2">Register</Link>
                        </p>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <input
                                type="text"
                                className="form-control form-control-lg"
                                placeholder="Place your link here"
                                value={originalUrl}
                                onChange={(e) => setOriginalUrl(e.target.value)}
                                required
                            />
                        </div>

                        <div className="mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Custom Alias (optional)"
                                value={customAlias}
                                onChange={handleAliasChange}
                                style={{ borderColor: aliasError ? "red" : "#ccc" }}
                                maxLength={20}
                            />
                            {aliasError && <small style={{color: "red"}}>{aliasError}</small>}
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary btn-lg w-100"
                            disabled={loading || !!aliasError}
                        >
                            {loading ? (
                                <span>
                                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                  Shortening...
                                </span>
                            ) : "Shorten URL"}
                        </button>
                    </form>

                    {error && (
                        <div className="alert alert-danger mt-3" role="alert">
                          {error}
                        </div>
                    )}

                    {shortUrl && (
                        <div className="mt-4 p-3 bg-success-subtle border border-success rounded">
                            <p className="mb-2 fw-bold text-success">Your short link:</p>
                            <a href={shortUrl} target="_blank" rel="noopener noreferrer" className="d-block h5 text-decoration-none text-success text-break">
                                {shortUrl}
                            </a>
                            <button onClick={copyToClipboard} className="btn btn-sm btn-outline-success mt-2">
                                Copy Link
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}

export default Home
