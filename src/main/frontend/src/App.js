import React, {useEffect, useState} from 'react';
import axios from 'axios';

function App() {
  const [hello, setHello] = useState('');

  const api = axios.create({
        baseURL: 'http://localhost:8080'
  });

  useEffect(() => {
      api.get('/api/hello')
        .then(response => setHello(response.data))
        .catch(error => console.log(error))
  }, []);

  return (
      <div>
        백엔드에서 가져온 데이터 : {hello}
      </div>
  );
}

export default App;