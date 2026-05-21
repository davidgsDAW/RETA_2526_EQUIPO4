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
        ctx.lineTo(300, 480);
        ctx.lineTo(300,100);
        ctx.lineTo(310, 100)
        ctx.moveTo(302, 100);
        ctx.lineTo(302, 40);
        ctx.moveTo(308, 100);
        ctx.lineTo(308, 40);
        ctx.moveTo(310, 40);
        ctx.lineTo(300, 40);
        ctx.lineTo(300, 30);
        ctx.lineTo(290, 30);
        ctx.lineTo(290, 20);
        ctx.moveTo(290, 28);
        ctx.lineTo(230, 28);
        ctx.moveTo(290, 22);
        ctx.lineTo(230, 22);
        ctx.moveTo(230, 20);
        ctx.lineTo(230, 30);
        ctx.lineTo(10, 30);
        ctx.lineTo(10, 60);
        ctx.moveTo(290, 20);
        ctx.lineTo(10, 20);
        ctx.stroke();
    }
}

// Dibujo del Armario del taller
function drawArmario() {
    const canvas = document.querySelector(".container-canvas-armario");

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

        
        
        ctx.stroke();
    }
}

window.addEventListener('DOMContentLoaded', drawTaller());
window.addEventListener('DOMContentLoaded', drawArmario());