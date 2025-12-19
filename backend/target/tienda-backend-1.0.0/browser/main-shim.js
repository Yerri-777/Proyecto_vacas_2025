
(function(){
  const root = document.getElementById('app-root') || document.body;
  root.innerHTML = `
    <div style="font-family:Arial,Helvetica,sans-serif;max-width:900px;margin:24px auto">
      <h1>Aplicación de prueba - Backend</h1>
      <p>probar endpoints del backend.</p>
      <div style="display:flex;gap:8px;margin-bottom:12px">
        <button id="btn-health">Comprobar /api/health</button>
        <button id="btn-empresa">GET /api/empresa</button>
        <button id="btn-categoria">GET /api/categoria</button>
      </div>
      <pre id="output" style="background:#f6f8fa;padding:12px;border-radius:6px;min-height:120px;white-space:pre-wrap"></pre>
    </div>
  `;

  const out = document.getElementById('output');
  function show(txt){ out.textContent = txt; }

  // Compute base path (app context) from the current URL so API calls work under any context
  function getAppBase(){
    const path = window.location.pathname || '/';
    const parts = path.split('/').filter(Boolean); // remove empties
    const bIndex = parts.indexOf('browser');
    if (bIndex > 0) {
      return '/' + parts.slice(0, bIndex).join('/');
    }
    // If 'browser' not present, try first segment as context (e.g. /tienda-backend/...)
    if (parts.length > 0 && parts[0] !== 'browser') {
      return '/' + parts[0];
    }
    return '';
  }

  async function call(path, opts){
    const base = getAppBase();
    const full = (path.startsWith('/') ? base + path : base + '/' + path).replace(/\/+/g,'/');
    show('Llamando ' + full + ' ...');
    try{
      const r = await fetch(full, opts);
      const t = await r.text();
      show('HTTP ' + r.status + '\n\n' + t);
    }catch(e){ show('Error: ' + e.message); }
  }

  document.getElementById('btn-health').addEventListener('click', ()=>call('/api/health'));
  document.getElementById('btn-empresa').addEventListener('click', ()=>call('/api/empresa'));
  document.getElementById('btn-categoria').addEventListener('click', ()=>call('/api/categoria'));
})();
