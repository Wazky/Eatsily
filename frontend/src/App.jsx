<<<<<<< HEAD
import { useEffect, useState } from 'react'
import './App.css'

function App() {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    testConnection();
  }, []);

  const testConnection = async () => {
    try {
      setLoading(true);
      // Usando el proxy configurado en vite.config.js
      const response = await fetch('/Eatsily/rest/pets');
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      console.log('Datos recibidos:', response);
      setData(await response.json());
    } catch (err) {
      setError(err.message);
      console.error('Error de conexión:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Probando conexión...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div style={{ padding: '20px' }}>
      <h1>Conexión Backend-Frontend</h1>
      
      <div style={{ marginBottom: '20px' }}>
        <button onClick={testConnection} style={{ marginRight: '10px' }}>
          Probar GET
        </button>
      </div>

      <h2>Datos recibidos:</h2>
      <pre style={{ 
        background: '#44334433', 
        padding: '10px', 
        borderRadius: '5px',
        maxHeight: '400px',
        overflow: 'auto'
      }}>
        {JSON.stringify(data, null, 2)}
      </pre>
    </div>
  );

=======
import { useState } from 'react'
import './App.css'

function App() {
  // Estados para registro
  const [name, setName] = useState('')
  const [surname, setSurname] = useState('')
  const [email, setEmail] = useState('')
  const [regUsername, setRegUsername] = useState('')
  const [regPassword, setRegPassword] = useState('')

  // Estados para login
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  
  // Estados generales
  const [token, setToken] = useState(localStorage.getItem('jwt') || '')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const [protectedData, setProtectedData] = useState(null)
  const [showRegister, setShowRegister] = useState(false) // Toggle entre login y registro

  const API_BASE_URL = '/DAAExample/rest'

  // ============= REGISTRO =============
  const handleRegister = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      const response = await fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ 
          name, 
          surname, 
          email, 
          username: regUsername, 
          password: regPassword 
        })
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`HTTP ${response.status}: ${errorText}`)
      }
      
      const data = await response.json()
      console.log('Register response:', data)

      if (data.token) {
        setToken(data.token)
        localStorage.setItem('jwt', data.token)
        setMessage('✅ Registro exitoso! Bienvenido!')
        // Limpiar formulario
        setName('')
        setSurname('')
        setEmail('')
        setRegUsername('')
        setRegPassword('')
      } else {
        setMessage('✅ Usuario registrado! Ahora puedes hacer login.')
        setShowRegister(false) // Cambiar a login
      }
    } catch (error) {
      console.error('Register error:', error)
      setMessage(`❌ Error en registro: ${error.message}`)
    } finally {
      setLoading(false)
    }
  }

  // ============= LOGIN =============
  const handleLogin = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')

    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password })
      })

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`HTTP ${response.status}: ${errorText}`)
      }

      const data = await response.json()
      console.log('Login response:', data)

      if (data.tokenResponse.accessToken) {
        setToken(data.tokenResponse.accessToken)
        localStorage.setItem('jwt', data.tokenResponse.accessToken)
        setMessage('✅ Login exitoso!')
        setPassword('')
      } else {
        setMessage(`❌ No se recibió token`)
      }
    } catch (error) {
      console.error('Login error:', error)
      setMessage(`❌ Error en login: ${error.message}`)
    } finally {
      setLoading(false)
    }
  }

  // ============= TEST ENDPOINT PÚBLICO =============

    const testPublicEndpoint = async () => {
    setLoading(true)
    setMessage('')
    setProtectedData(null)

    try {
      const response = await fetch(`${API_BASE_URL}/people`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json'  
        }
      })

      if (response.ok) {
        const data = await response.json()
        setProtectedData(data)
        setMessage('✅ Datos públicos obtenidos correctamente')
      } else {
        const error = await response.text()
        setMessage(`❌ Error ${response.status}: ${error}`)
      }
    } catch (error) {
      setMessage(`❌ Error: ${error.message}`)
    } finally {
      setLoading(false)
    }
    }

  // ============= TEST ENDPOINT PROTEGIDO =============
  const testProtectedEndpoint = async () => {
    setLoading(true)
    setMessage('')
    setProtectedData(null)

    try {
      const response = await fetch(`${API_BASE_URL}/auth/securedPing`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      console.log('Protected endpoint response status:', response)

      if (response.ok) {
        const data = await response.json()
        setProtectedData(data)
        setMessage('✅ Datos obtenidos correctamente')
      } else {
        const error = await response.text()
        setMessage(`❌ Error ${response.status}: ${error}`)
      }
    } catch (error) {
      setMessage(`❌ Error: ${error.message}`)
    } finally {
      setLoading(false)
    }
  }

  // ============= LOGOUT =============
  const handleLogout = () => {
    setToken('')
    localStorage.removeItem('jwt')
    setMessage('✅ Sesión cerrada')
    setProtectedData(null)
  }

  return (
    <div className="App">
      <h1>🔐 Test de Autenticación JWT</h1>
      
      {/* Estado del token */}
      <div className="token-status">
        <h3>Estado: {token ? '🟢 Autenticado' : '🔴 No autenticado'}</h3>
        {token && (
          <div className="token-display">
            <p><strong>Token:</strong></p>
            <code>{token.substring(0, 50)}...</code>
          </div>
        )}
      </div>

      {/* Formularios de Login/Registro o Panel autenticado */}
      {!token ? (
        <div className="auth-section">
          {/* Toggle entre Login y Registro */}
          <div className="auth-toggle">
            <button 
              className={!showRegister ? 'active' : ''}
              onClick={() => setShowRegister(false)}
            >
              Login
            </button>
            <button 
              className={showRegister ? 'active' : ''}
              onClick={() => setShowRegister(true)}
            >
              Registro
            </button>
          </div>

          <div className="test-buttons">
            <button onClick={testPublicEndpoint} disabled={loading}>
              🧪 Test endpoint público
            </button>
          </div>

          {/* FORMULARIO DE LOGIN */}
          {!showRegister ? (
            <div className="login-section">
              <h2>Iniciar Sesión</h2>
              <form onSubmit={handleLogin}>
                <div className="form-group">
                  <label>Username</label>
                  <input
                    type="text"
                    placeholder="Ingresa tu username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Password</label>
                  <input
                    type="password"
                    placeholder="Ingresa tu contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
                <button type="submit" disabled={loading}>
                  {loading ? '⏳ Cargando...' : '🔑 Iniciar Sesión'}
                </button>
              </form>
              <p className="switch-form">
                ¿No tienes cuenta? 
                <button onClick={() => setShowRegister(true)}>Regístrate aquí</button>
              </p>
            </div>
          ) : (
            /* FORMULARIO DE REGISTRO */
            <div className="register-section">
              <h2>Crear Cuenta</h2>
              <form onSubmit={handleRegister}>
                <div className="form-row">
                  <div className="form-group">
                    <label>Nombre</label>
                    <input
                      type="text"
                      placeholder="Juan"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Apellido</label>
                    <input
                      type="text"
                      placeholder="Pérez"
                      value={surname}
                      onChange={(e) => setSurname(e.target.value)}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label>Email</label>
                  <input
                    type="email"
                    placeholder="juan@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Username</label>
                  <input
                    type="text"
                    placeholder="juanperez"
                    value={regUsername}
                    onChange={(e) => setRegUsername(e.target.value)}
                    required
                    minLength={3}
                  />
                  <small>Mínimo 3 caracteres</small>
                </div>

                <div className="form-group">
                  <label>Password</label>
                  <input
                    type="password"
                    placeholder="••••••••"
                    value={regPassword}
                    onChange={(e) => setRegPassword(e.target.value)}
                    required
                    minLength={6}
                  />
                  <small>Mínimo 6 caracteres</small>
                </div>

                <button type="submit" disabled={loading}>
                  {loading ? '⏳ Registrando...' : '✨ Crear Cuenta'}
                </button>
              </form>
              <p className="switch-form">
                ¿Ya tienes cuenta? 
                <button onClick={() => setShowRegister(false)}>Inicia sesión aquí</button>
              </p>
            </div>
          )}
        </div>
      ) : (
        /* PANEL AUTENTICADO */
        <div className="authenticated-section">
          <h2>Panel de pruebas</h2>
          <button onClick={handleLogout} className="logout-btn">
            🚪 Cerrar sesión
          </button>
          
          <div className="test-buttons">
            <button onClick={testProtectedEndpoint} disabled={loading}>
              🧪 Test endpoint protegido
            </button>
          </div>

          {/* Mostrar datos obtenidos */}
          {protectedData && (
            <div className="data-display">
              <h3>✅ Datos recibidos:</h3>
              <pre>{JSON.stringify(protectedData, null, 2)}</pre>
            </div>
          )}
        </div>
      )}

      {/* Mensajes */}
      {message && (
        <div className={`message ${message.includes('❌') ? 'error' : 'success'}`}>
          {message}
        </div>
      )}
    </div>
  )
>>>>>>> 1c06d344d4f85a133090b7f860829be46427d4bc
}

export default App