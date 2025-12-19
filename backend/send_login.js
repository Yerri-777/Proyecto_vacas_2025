const fs = require('fs');
const url = 'http://localhost:8080/tienda-backend/api/auth';
(async ()=>{
  try{
    const body = JSON.stringify({ correo: 'admin@tienda.com', password: 'admin123' });
    const res = await fetch(url, {method:'POST', headers:{'Content-Type':'application/json; charset=utf-8'}, body, redirect:'manual'});
    const text = await res.text();
    console.log('Status:', res.status);
    console.log('Headers:', Object.fromEntries(res.headers));
    console.log('Body:', text);
  }catch(e){ console.error('Error', e.message); process.exit(1);} 
})();
