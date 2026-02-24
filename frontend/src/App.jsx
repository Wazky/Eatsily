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

}

export default App
