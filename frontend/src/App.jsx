import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import axios from 'axios'

function App() {
    const [originalUrl, setOriginalUrl] = useState('')
    const [customAlias, setCustomAlias] = useState('')
    const [shortUrl, setShortUrl] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

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
            const response = await axios.post('/api/shorten', {
                originalUrl: originalUrl,
                customAlias: customAlias
            })
            const shortenUrl = `${window.location.origin}/${response.data}`
            setShortUrl(shortenUrl)

            const serverMessage = typeof err.response?.data === 'string' ? err.response.data : err.response?.data?.message;

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
        <div className="container d-flex flex-column align-items-center justify-content-center min-vh-100 bg-light">

            <div className="card shadow-lg p-4" style={{ maxWidth: '500px', width: '100%' }}>
                <div className="card-body text-center">
                    <h1 className="card-title mb-3">🔗 URL Shortener</h1>
                    <p className="card-text text-muted mb-4">
                        Enter a link and we will shorten it for you!
                    </p>

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
                                placeholder="Custom Alias"
                                value={customAlias}
                                onChange={(e) => setCustomAlias(e.target.value)}
                                maxLength={20}
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary btn-lg w-100"
                            disabled={loading}
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

export default App
