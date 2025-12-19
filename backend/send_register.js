const fetch = require('node-fetch');
(async ()=>{
  try{
    const url = 'http://localhost:8080/tienda-backend/api/account';
    const body = JSON.stringify({ correo: 'newuser@example.com', password: 'test123', rol: 'USUARIO' });
    const res = await fetch(url, { method: 'POST', headers: {'Content-Type':'application/json'}, body });
    const text = await res.text();
    console.log('Status:', res.status);
    console.log('Body:', text);
  }catch(e){ console.error('Error', e.message); process.exit(1);} 
})();
