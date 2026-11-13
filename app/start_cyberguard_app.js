/**
 * CyberGuard AI Dual Application Launcher
 * Boots REST Backend (:8000) and Responsive Web Frontend (:5173) simultaneously.
 */

const { fork } = require('child_process');
const path = require('path');

console.log('====================================================');
console.log('   Starting CyberGuard AI Platform (AUT)...');
console.log('====================================================');

const backend = fork(path.join(__dirname, 'backend', 'server.js'));
const frontend = fork(path.join(__dirname, 'frontend', 'server.js'));

backend.on('error', (err) => console.error('[Backend Error]', err));
frontend.on('error', (err) => console.error('[Frontend Error]', err));

process.on('SIGINT', () => {
  backend.kill();
  frontend.kill();
  process.exit();
});

