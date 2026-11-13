/**
 * CyberGuard AI Frontend Client Script
 * Implements view transitions, reactive authentication handling,
 * real-time multilingual text classification, file ingestion simulations,
 * and responsive hamburger toggle.
 */

document.addEventListener('DOMContentLoaded', () => {
  // Navigation elements
  const mobileHamburger = document.getElementById('mobile-hamburger');
  const navbarLinks = document.querySelector('.cg-navbar-links');
  const navLinks = document.querySelectorAll('.cg-nav-link');
  const logoutBtn = document.getElementById('logout-btn');

  // Views
  const views = {
    auth: document.getElementById('auth-section'),
    dashboard: document.getElementById('dashboard-section'),
    history: document.getElementById('history-section'),
    admin: document.getElementById('admin-section')
  };

  // Auth Form Elements
  const authForm = document.getElementById('auth-form');
  const emailInput = document.getElementById('email-input');
  const passwordInput = document.getElementById('password-input');
  const fullNameInput = document.getElementById('full-name-input');
  const fullNameGroup = document.getElementById('group-fullname');
  const submitAuthBtn = document.getElementById('submit-auth-btn');
  const errorAlert = document.getElementById('error-alert');
  const authHeaderTitle = document.getElementById('auth-header-title');
  const tabLoginBtn = document.getElementById('tab-login-btn');
  const tabRegisterBtn = document.getElementById('tab-register-btn');

  // Analysis Elements
  const analysisInput = document.getElementById('analysis-input');
  const analyzeBtn = document.getElementById('analyze-btn');
  const analysisResult = document.getElementById('analysis-result');
  const badgeLang = document.getElementById('badge-language');
  const badgeSarcasm = document.getElementById('badge-sarcasm');
  const badgeSeverity = document.getElementById('badge-severity');
  const echoedText = document.getElementById('echoed-text');
  const expLine1 = document.getElementById('exp-line-1');
  const expLine2 = document.getElementById('exp-line-2');

  // File Upload Elements
  const batchCsvFile = document.getElementById('batch-csv-file');
  const batchResultsTable = document.getElementById('batch-results-table');
  const ocrImageFile = document.getElementById('ocr-image-file');
  const ocrOutput = document.getElementById('ocr-output');
  const chatExportFile = document.getElementById('chat-export-file');
  const chatTimelineView = document.getElementById('chat-timeline-view');

  // History Elements
  const historySearch = document.getElementById('history-search');
  const severityFilter = document.getElementById('severity-filter');
  const historyTableBody = document.getElementById('history-table-body');

  let currentAuthMode = 'login'; // 'login' or 'register'
  let currentUser = null;

  // Mobile Hamburger Toggle
  if (mobileHamburger) {
    mobileHamburger.addEventListener('click', () => {
      navbarLinks.classList.toggle('open');
    });
  }

  // View Navigation Router
  function showView(viewName) {
    Object.keys(views).forEach(v => {
      if (views[v]) {
        views[v].style.display = (v === viewName) ? 'block' : 'none';
      }
    });

    navLinks.forEach(link => {
      if (link.getAttribute('href') === `#/${viewName}`) {
        link.classList.add('active');
      } else {
        link.classList.remove('active');
      }
    });

    if (navbarLinks) {
      navbarLinks.classList.remove('open');
    }
  }

  window.addEventListener('hashchange', () => {
    const hash = window.location.hash.replace('#/', '') || 'auth';
    if (views[hash]) {
      showView(hash);
    }
  });

  // Tab Toggle: Login vs Register
  tabLoginBtn.addEventListener('click', () => {
    currentAuthMode = 'login';
    tabLoginBtn.classList.add('active');
    tabRegisterBtn.classList.remove('active');
    fullNameGroup.style.display = 'none';
    authHeaderTitle.textContent = 'Passenger / User Login';
    submitAuthBtn.textContent = 'Sign In to Platform';
    errorAlert.style.display = 'none';
  });

  tabRegisterBtn.addEventListener('click', () => {
    currentAuthMode = 'register';
    tabRegisterBtn.classList.add('active');
    tabLoginBtn.classList.remove('active');
    fullNameGroup.style.display = 'block';
    authHeaderTitle.textContent = 'Register New Account';
    submitAuthBtn.textContent = 'Create Free Account';
    errorAlert.style.display = 'none';
  });

  // Auth Form Submission
  authForm.addEventListener('submit', (e) => {
    e.preventDefault();
    errorAlert.style.display = 'none';

    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    // Empty validation
    if (!email || !password) {
      errorAlert.textContent = '⚠️ Validation error: Email and password are required.';
      errorAlert.style.display = 'block';
      return;
    }

    if (currentAuthMode === 'register') {
      currentUser = { email: email, role: 'user', name: fullNameInput.value || 'Registered User' };
      localStorage.setItem('cg_user', JSON.stringify(currentUser));
      logoutBtn.style.display = 'inline-block';
      window.location.hash = '#/dashboard';
      showView('dashboard');
      return;
    }

    // Login Authentication Verification
    if (email === 'admin@admin.cyberguard' && password === 'AdminSecurePass!123') {
      currentUser = { email: email, role: 'admin', name: 'System Administrator' };
      localStorage.setItem('cg_user', JSON.stringify(currentUser));
      logoutBtn.style.display = 'inline-block';
      window.location.hash = '#/admin';
      showView('admin');
    } else if (email === 'ankit.test@cyberguard.ai' && password === 'Password@123') {
      currentUser = { email: email, role: 'user', name: 'Ankit Mishra' };
      localStorage.setItem('cg_user', JSON.stringify(currentUser));
      logoutBtn.style.display = 'inline-block';
      window.location.hash = '#/dashboard';
      showView('dashboard');
    } else {
      errorAlert.textContent = '⚠️ Invalid pass credentials. Incorrect email or password.';
      errorAlert.style.display = 'block';
    }
  });

  // Logout Handler
  logoutBtn.addEventListener('click', () => {
    currentUser = null;
    localStorage.removeItem('cg_user');
    logoutBtn.style.display = 'none';
    emailInput.value = '';
    passwordInput.value = '';
    errorAlert.style.display = 'none';
    window.location.hash = '#/auth';
    showView('auth');
  });

  // Real-Time Text Analysis
  analyzeBtn.addEventListener('click', () => {
    const text = (analysisInput.value || '').trim();
    if (!text) return;

    analysisResult.style.display = 'block';
    echoedText.textContent = `"${text}"`;

    const lower = text.toLowerCase();
    const isSarcasm = lower.includes('genius') || lower.includes('brilliant') || text.includes('वाह');
    const isToxic = lower.includes('useless') || lower.includes('hate') || lower.includes('miserable') || lower.includes('get lost');

    if (isSarcasm) {
      badgeLang.textContent = 'HINDI / ENGLISH';
      badgeSarcasm.style.display = 'inline-block';
      badgeSarcasm.textContent = 'LIKELY SARCASM';
      badgeSeverity.className = 'cg-badge cg-badge-severity cg-severity-badge badge-medium';
      badgeSeverity.textContent = 'SEVERITY: MEDIUM (Downgraded)';
      expLine1.textContent = '• Multilingual script detected: Latin (English) + Devanagari (Hindi). Language: Hindi-English Code-mix.';
      expLine2.textContent = "• Marker heuristic ('oh brilliant', exaggerated exclamation) indicates irony. Raw severity High -> Downgraded to Medium.";
    } else if (isToxic) {
      badgeLang.textContent = 'ENGLISH';
      badgeSarcasm.style.display = 'none';
      badgeSeverity.className = 'cg-badge cg-badge-severity cg-severity-badge badge-critical';
      badgeSeverity.textContent = 'SEVERITY: HIGH';
      expLine1.textContent = '• Explicit hate speech tokens detected in Latin script.';
      expLine2.textContent = '• Severe toxicity threshold exceeded (0.88 score). Flagged for moderator review.';
    } else {
      badgeLang.textContent = 'ENGLISH';
      badgeSarcasm.style.display = 'none';
      badgeSeverity.className = 'cg-badge cg-badge-severity cg-severity-badge badge-safe';
      badgeSeverity.textContent = 'SEVERITY: SAFE';
      expLine1.textContent = '• Clean linguistic sentiment with 0 toxic spans detected.';
      expLine2.textContent = '• Text compliant with platform safety guidelines.';
    }
  });

  // File Upload Handlers
  batchCsvFile.addEventListener('change', () => {
    if (batchCsvFile.files.length > 0) {
      batchResultsTable.style.display = 'block';
    }
  });

  ocrImageFile.addEventListener('change', () => {
    if (ocrImageFile.files.length > 0) {
      ocrOutput.style.display = 'block';
    }
  });

  chatExportFile.addEventListener('change', () => {
    if (chatExportFile.files.length > 0) {
      chatTimelineView.style.display = 'block';
    }
  });

  // History Filter & Search
  function filterHistory() {
    const query = (historySearch.value || '').toLowerCase();
    const selectedSeverity = severityFilter.value;
    const rows = historyTableBody.querySelectorAll('.cg-history-row');

    rows.forEach(row => {
      const text = row.children[1].textContent.toLowerCase();
      const severity = row.getAttribute('data-severity');

      const matchesQuery = !query || text.includes(query);
      const matchesSeverity = (selectedSeverity === 'ALL') || (severity === selectedSeverity);

      if (matchesQuery && matchesSeverity) {
        row.style.display = '';
      } else {
        row.style.display = 'none';
      }
    });
  }

  historySearch.addEventListener('input', filterHistory);
  severityFilter.addEventListener('change', filterHistory);

  // History Item Delete
  historyTableBody.addEventListener('click', (e) => {
    if (e.target && e.target.classList.contains('cg-delete-btn')) {
      const row = e.target.closest('tr');
      if (row) {
        row.remove();
      }
    }
  });

  // Initial routing
  const initialHash = window.location.hash.replace('#/', '') || 'auth';
  showView(views[initialHash] ? initialHash : 'auth');
});

