import { instantiate } from './web-canvas.uninstantiated.mjs';

await wasmSetup;
instantiate({ skia: Module['asm'] });
