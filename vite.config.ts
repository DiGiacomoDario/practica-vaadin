import { UserConfigFn } from 'vite';
import { overrideVaadinConfig } from './vite.generated';

const customConfig: UserConfigFn = (env) => ({
    server: {
        strictPort: true,
        port: 60649, // Usando el puerto que aparece en tus logs
        hmr: {
            protocol: 'ws',
            host: 'localhost',
            port: 60649,
            clientPort: 60649
        },
        fs: {
            strict: false,
            allow: [
                '/VAADIN/',
                '/frontend/',
                '/node_modules/'
            ]
        },
        watch: {
            usePolling: true,
            interval: 1000
        }
    },
    build: {
        chunkSizeWarningLimit: 2000,
        rollupOptions: {
            output: {
                manualChunks: {
                    vaadin: [
                        '@vaadin/button',
                        '@vaadin/text-field',
                        '@vaadin/grid',
                        '@vaadin/notification',
                        '@vaadin/router'
                    ],
                    vendors: [
                        'lit',
                        '@lit/reactive-element',
                        'lit-html',
                        'lit-element'
                    ]
                }
            }
        }
    },
    optimizeDeps: {
        include: [
            '@vaadin/button',
            '@vaadin/text-field',
            '@vaadin/grid',
            '@vaadin/notification',
            '@vaadin/router',
            'lit',
            '@lit/reactive-element'
        ],
        exclude: [
            '@vaadin/flow-frontend',
            '@vaadin/vaadin-progress-bar'
        ]
    },
    plugins: []
});

export default overrideVaadinConfig(customConfig);