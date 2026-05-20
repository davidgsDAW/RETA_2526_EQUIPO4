// Dibujo del Taller
function drawTaller() {
    const canvas = document.querySelector(".container-canvas");

    if(!canvas) return;

    if (canvas.getContext) {
        // Gracias a esta variable 'rect' consigo que
        // el Canvas no salga estirado, gracias a las
        // dimensiones reales del rectángulo donde está
        const rect = canvas.getBoundingClientRect();
        canvas.width = rect.width;
        canvas.height = rect.height;

        // Defino el trazado en "2D" y dibujo el taller
        const ctx = canvas.getContext("2d");

        ctx.beginPath();
        ctx.moveTo(10, 60);
        ctx.lineTo(10, 300);
        ctx.lineTo(100, 300);
        ctx.lineTo(100, 310);
        ctx.lineTo(10, 310);
        ctx.moveTo(98, 310);
        ctx.lineTo(98, 360);
        ctx.moveTo(92, 310);
        ctx.lineTo(92, 360);
        ctx.moveTo(90, 360);
        ctx.lineTo(90, 480);
        ctx.moveTo(90, 360);
        ctx.lineTo(100, 360);
        ctx.lineTo(100, 480);
        ctx.stroke();
    }
}

window.addEventListener('DOMContentLoaded', drawTaller());