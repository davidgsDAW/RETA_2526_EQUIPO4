document.addEventListener('DOMContentLoaded', () => {
    const zones = document.querySelectorAll('.zone');
    const viewerImg = document.getElementById('viewer-img');
    const placeholder = document.getElementById('viewer-placeholder');

    zones.forEach(zone => {
        zone.addEventListener('click', () => {
            const imageSrc = zone.getAttribute('data-img');
            
            // Actualizamos la imagen
            viewerImg.src = imageSrc;
            viewerImg.style.display = 'block';
            
            // Ocultamos el texto de ayuda
            placeholder.style.display = 'none';

            // Efecto visual simple de selección
            zones.forEach(z => z.style.outline = 'none');
            zone.style.outline = '4px solid #2ecc71';
        });
    });
});