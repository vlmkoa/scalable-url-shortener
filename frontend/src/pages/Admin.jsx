import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const ROLES = ['FREE', 'PREMIUM', 'ADMIN'];
const STATUSES = ['ACTIVE', 'SUSPENDED', 'BANNED'];

function Admin() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editing, setEditing] = useState({}); // { id: { role, status } }
  const [savingId, setSavingId] = useState(null);

  useEffect(() => {
    if (!user) {
      navigate('/login', { replace: true });
      return;
    }
    if (user.role !== 'ADMIN') {
      navigate('/', { replace: true });
      return;
    }
    fetchUsers();
  }, [user, navigate]);

  const authHeaders = user?.token ? { Authorization: `Bearer ${user.token}` } : {};

  const fetchUsers = async () => {
    setLoading(true);
    setError('');
    try {
      const { data } = await axios.get('/api/admin/users', { headers: authHeaders });
      setUsers(Array.isArray(data) ? data : []);
      setEditing({});
    } catch (err) {
      setError(err.response?.status === 403 ? 'Access denied' : err.response?.data || 'Failed to load users');
      setUsers([]);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (u) => {
    setEditing((prev) => ({ ...prev, [u.id]: { role: u.role, status: u.status } }));
  };

  const handleChange = (id, field, value) => {
    setEditing((prev) => ({
      ...prev,
      [id]: { ...(prev[id] || {}), [field]: value },
    }));
  };

  const handleSave = async (id) => {
    const payload = editing[id];
    if (!payload) return;
    setSavingId(id);
    setError('');
    try {
      await axios.patch(`/api/admin/users/${id}`, payload, { headers: authHeaders });
      setUsers((prev) =>
        prev.map((u) => (u.id === id ? { ...u, role: payload.role, status: payload.status } : u))
      );
      setEditing((prev) => {
        const next = { ...prev };
        delete next[id];
        return next;
      });
    } catch (err) {
      setError(err.response?.data || 'Failed to update user');
    } finally {
      setSavingId(null);
    }
  };

  const handleCancel = (id) => {
    setEditing((prev) => {
      const next = { ...prev };
      delete next[id];
      return next;
    });
  };

  if (!user) return null;
  if (user.role !== 'ADMIN') return null;

  return (
    <div className="container-fluid min-vh-100 py-4 bg-light">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="h4 mb-0">Admin – Users</h1>
        <div className="d-flex align-items-center gap-2">
          <Link to="/" className="btn btn-outline-primary btn-sm">Home</Link>
          <span className="text-muted small">{user.email}</span>
          <button type="button" className="btn btn-outline-secondary btn-sm" onClick={logout}>
            Log out
          </button>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      {loading ? (
        <p className="text-muted">Loading users…</p>
      ) : (
        <div className="card shadow-sm">
          <div className="table-responsive">
            <table className="table table-hover mb-0">
              <thead className="table-light">
                <tr>
                  <th>ID</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th className="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => {
                  const isEditing = editing[u.id] !== undefined;
                  const edit = editing[u.id] || { role: u.role, status: u.status };
                  return (
                    <tr key={u.id}>
                      <td>{u.id}</td>
                      <td>{u.email}</td>
                      <td>
                        {isEditing ? (
                          <select
                            className="form-select form-select-sm"
                            value={edit.role}
                            onChange={(e) => handleChange(u.id, 'role', e.target.value)}
                          >
                            {ROLES.map((r) => (
                              <option key={r} value={r}>{r}</option>
                            ))}
                          </select>
                        ) : (
                          <span className={`badge ${u.role === 'ADMIN' ? 'bg-danger' : u.role === 'PREMIUM' ? 'bg-primary' : 'bg-secondary'}`}>
                            {u.role}
                          </span>
                        )}
                      </td>
                      <td>
                        {isEditing ? (
                          <select
                            className="form-select form-select-sm"
                            value={edit.status}
                            onChange={(e) => handleChange(u.id, 'status', e.target.value)}
                          >
                            {STATUSES.map((s) => (
                              <option key={s} value={s}>{s}</option>
                            ))}
                          </select>
                        ) : (
                          <span className={`badge ${u.status === 'ACTIVE' ? 'bg-success' : u.status === 'BANNED' ? 'bg-danger' : 'bg-warning text-dark'}`}>
                            {u.status}
                          </span>
                        )}
                      </td>
                      <td className="text-end">
                        {isEditing ? (
                          <>
                            <button
                              type="button"
                              className="btn btn-success btn-sm me-1"
                              disabled={savingId === u.id}
                              onClick={() => handleSave(u.id)}
                            >
                              {savingId === u.id ? 'Saving…' : 'Save'}
                            </button>
                            <button
                              type="button"
                              className="btn btn-outline-secondary btn-sm"
                              onClick={() => handleCancel(u.id)}
                            >
                              Cancel
                            </button>
                          </>
                        ) : (
                          <button
                            type="button"
                            className="btn btn-outline-primary btn-sm"
                            onClick={() => handleEdit(u)}
                          >
                            Edit
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
          {users.length === 0 && !loading && (
            <div className="card-body text-center text-muted">No users found.</div>
          )}
        </div>
      )}
    </div>
  );
}

export default Admin;
