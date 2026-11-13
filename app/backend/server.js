/**
 * CyberGuard AI - Mock & Automated Testing REST Backend
 * Emulates FastAPI backend on http://localhost:8000/api
 * Meets all specifications of CyberGuardApiTest and JMeter performance benchmarks.
 */

const http = require('http');
const url = require('url');

const PORT = process.env.PORT || 8000;

// In-memory data store seeded with values from Chapter 1, 3, and 6
const users = {
    'ankit.test@cyberguard.ai': {
        id: 'usr-1001',
        name: 'Ankit Mishra',
        email: 'ankit.test@cyberguard.ai',
        password: 'Password@123',
        role: 'user'
    },
    'admin@admin.cyberguard': {
        id: 'adm-2001',
        name: 'System Administrator',
        email: 'admin@admin.cyberguard',
        password: 'AdminSecurePass!123',
        role: 'admin'
    }
};

let historyRecords = [
    {
        id: 'ana-01',
        text: 'Oh great, another brilliant genius who knows everything! वाह क्या बात है!',
        is_safe: false,
        severity: 'Medium',
        category: 'Harassment',
        is_sarcastic: true,
        language: 'Hindi-English',
        timestamp: '2026-10-28 10:14:00'
    },
    {
        id: 'ana-02',
        text: 'You are completely useless, nobody wants you here. Get lost!',
        is_safe: false,
        severity: 'High',
        category: 'Hate Speech',
        is_sarcastic: false,
        language: 'English',
        timestamp: '2026-10-28 10:20:00'
    },
    {
        id: 'ana-03',
        text: 'Good morning everyone! Hope you have a wonderful and productive day ahead.',
        is_safe: true,
        severity: 'Safe',
        category: 'General',
        is_sarcastic: false,
        language: 'English',
        timestamp: '2026-10-28 10:30:00'
    }
];

function sendJson(res, statusCode, data) {
    const jsonStr = JSON.stringify(data);
    res.writeHead(statusCode, {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(jsonStr),
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization'
    });
    res.end(jsonStr);
}

function parseBody(req) {
    return new Promise((resolve, reject) => {
        let body = '';
        req.on('data', chunk => body += chunk);
        req.on('end', () => {
            if (!body) return resolve({});
            try {
                // If multipart, simple extraction
                if (req.headers['content-type'] && req.headers['content-type'].includes('multipart/form-data')) {
                    resolve({ raw: body, file: 'uploaded_file' });
                } else {
                    resolve(JSON.parse(body));
                }
            } catch (err) {
                resolve({ raw: body });
            }
        });
        req.on('error', reject);
    });
}

function verifyToken(authHeader) {
    if (!authHeader || !authHeader.startsWith('Bearer ')) return null;
    const token = authHeader.substring(7).trim();
    if (token.includes('admin') || token === 'admin-jwt-token-xyz') {
        return { email: 'admin@admin.cyberguard', role: 'admin' };
    }
    if (token.includes('user') || token.length > 5) {
        return { email: 'ankit.test@cyberguard.ai', role: 'user' };
    }
    return null;
}

const server = http.createServer(async (req, res) => {
    const parsedUrl = url.parse(req.url, true);
    const pathname = parsedUrl.pathname;
    const method = req.method;

    // Handle CORS preflight
    if (method === 'OPTIONS') {
        res.writeHead(204, {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
            'Access-Control-Allow-Headers': 'Content-Type, Authorization'
        });
        return res.end();
    }

    // Health Check
    if (pathname === '/api/health' && method === 'GET') {
        return sendJson(res, 200, { status: 'ok', uptime: process.uptime() });
    }

    // Auth Login: POST /api/auth/login
    if (pathname === '/api/auth/login' && method === 'POST') {
        const body = await parseBody(req);
        const { email, password } = body;
        const user = users[email];

        if (user && user.password === password) {
            const token = 'jwt-token-' + (user.role === 'admin' ? 'admin-' : 'user-') + Date.now();
            return sendJson(res, 200, {
                access_token: token,
                token_type: 'bearer',
                role: user.role,
                name: user.name,
                email: user.email
            });
        } else {
            return sendJson(res, 401, { detail: 'Invalid pass credentials. Incorrect email or password.' });
        }
    }

    // Auth Register: POST /api/auth/register
    if (pathname === '/api/auth/register' && method === 'POST') {
        const body = await parseBody(req);
        const { fullName, email, password } = body;
        if (!email || !password) {
            return sendJson(res, 400, { detail: 'Missing required credentials' });
        }
        users[email] = {
            id: 'usr-' + Math.floor(Math.random() * 9000 + 1000),
            name: fullName || 'New User',
            email: email,
            password: password,
            role: 'user'
        };
        const token = 'jwt-token-user-' + Date.now();
        return sendJson(res, 201, {
            access_token: token,
            token_type: 'bearer',
            role: 'user',
            name: fullName,
            email: email
        });
    }

    // Current User Profile: GET /api/auth/me
    if (pathname === '/api/auth/me' && method === 'GET') {
        const authUser = verifyToken(req.headers['authorization']);
        if (!authUser) {
            return sendJson(res, 401, { detail: 'Not authenticated' });
        }
        const profile = users[authUser.email] || { email: authUser.email, role: authUser.role };
        return sendJson(res, 200, profile);
    }

    // Analyze Text: POST /api/analyze/text
    if (pathname === '/api/analyze/text' && method === 'POST') {
        const authUser = verifyToken(req.headers['authorization']);
        if (!authUser) {
            return sendJson(res, 401, { detail: 'Missing or invalid token' });
        }
        const body = await parseBody(req);
        const text = body.text || '';

        const isSarcasm = text.toLowerCase().includes('brilliant') || 
                          text.toLowerCase().includes('genius') || 
                          text.includes('वाह') || 
                          text.toLowerCase().includes('sarcasm');
        
        const isToxic = text.toLowerCase().includes('useless') || 
                        text.toLowerCase().includes('hate') || 
                        text.toLowerCase().includes('kill') || 
                        text.toLowerCase().includes('miserable') || 
                        text.toLowerCase().includes('clown') ||
                        text.toLowerCase().includes('get lost');

        let severity = 'Safe';
        let isSafe = true;
        let toxicityScore = 0.05;

        if (isToxic) {
            isSafe = false;
            severity = isSarcasm ? 'Medium' : 'High';
            toxicityScore = isSarcasm ? 0.45 : 0.88;
        } else if (isSarcasm) {
            severity = 'Medium';
            toxicityScore = 0.40;
            isSafe = false;
        }

        const newRecord = {
            id: 'ana-' + (historyRecords.length + 1),
            text: text,
            is_safe: isSafe,
            severity: severity,
            toxicity_score: toxicityScore,
            is_sarcastic: isSarcasm,
            language: text.match(/[\u0900-\u097F]/) ? 'Hindi-English' : 'English',
            timestamp: new Date().toISOString()
        };
        historyRecords.unshift(newRecord);

        return sendJson(res, 200, {
            id: newRecord.id,
            is_safe: isSafe,
            severity: severity,
            toxicity_score: toxicityScore,
            is_sarcastic: isSarcasm,
            language: newRecord.language,
            explanation: isSarcasm 
                ? "Multilingual script detected. Marker heuristic indicates irony. Raw severity High -> Downgraded to Medium."
                : (isSafe ? "Safe status; 0 toxic spans detected." : "High severity toxic markers identified.")
        });
    }

    // Analyze Batch CSV: POST /api/analyze/csv
    if (pathname === '/api/analyze/csv' && method === 'POST') {
        const authUser = verifyToken(req.headers['authorization']);
        if (!authUser) {
            return sendJson(res, 401, { detail: 'Unauthorized' });
        }
        return sendJson(res, 200, {
            total_rows: 5,
            flagged_rows: 3,
            safe_rows: 2,
            summary: "5 rows evaluated; flags listed: 3 toxic markers, 1 sarcasm heuristic."
        });
    }

    // Get History: GET /api/analyze/history
    if (pathname === '/api/analyze/history' && method === 'GET') {
        const authUser = verifyToken(req.headers['authorization']);
        if (!authUser) {
            return sendJson(res, 401, { detail: 'Unauthorized' });
        }
        return sendJson(res, 200, { history: historyRecords });
    }

    // Delete History Item: DELETE /api/analyze/history/:id
    if (pathname.startsWith('/api/analyze/history/') && method === 'DELETE') {
        const authUser = verifyToken(req.headers['authorization']);
        if (!authUser) {
            return sendJson(res, 401, { detail: 'Unauthorized' });
        }
        const id = pathname.substring('/api/analyze/history/'.length);
        historyRecords = historyRecords.filter(r => r.id !== id);
        return sendJson(res, 200, { deleted: true, id: id });
    }

    // Admin Stats: GET /api/admin/stats
    if (pathname === '/api/admin/stats' && method === 'GET') {
        const authUser = verifyToken(req.headers['authorization']);
        if (!authUser) {
            return sendJson(res, 401, { detail: 'Unauthorized' });
        }
        if (authUser.role !== 'admin') {
            return sendJson(res, 403, { detail: 'Forbidden: Admin privilege required' });
        }
        return sendJson(res, 200, {
            total_analyses: '1,248',
            flagged_toxic: '412',
            sarcasm_ratio: '15.1%',
            registered_users: '86',
            language_breakdown: {
                english: '45.2% (564 scans)',
                hindi: '28.4% (354 scans)',
                punjabi: '14.1% (176 scans)',
                tamil_telugu: '12.3% (154 scans)'
            },
            top_flagged_senders: [
                { handle: '@troll_account_99', toxicity: '88% Toxic (Critical)' },
                { handle: '@spammer_bot', toxicity: '74% Toxic (High)' },
                { handle: '+91 98765 43210 (WhatsApp)', toxicity: '62% Toxic (High)' }
            ]
        });
    }

    // 404 Fallback
    return sendJson(res, 404, { detail: 'Endpoint not found' });
});

server.listen(PORT, '0.0.0.0', () => {
    console.log(`[CyberGuard API Server] Running on http://localhost:${PORT}/api`);
});

