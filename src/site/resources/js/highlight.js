/* Syntax Highlighting with highlight.js */
(function() {
    if (typeof hljs !== 'undefined') {
        hljs.highlightAll();
    } else {
        // Load highlight.js if not already loaded
        var link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css';
        document.head.appendChild(link);

        var script = document.createElement('script');
        script.src = 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js';
        script.onload = function() {
            hljs.highlightAll();
        };
        document.head.appendChild(script);
    }
})();
