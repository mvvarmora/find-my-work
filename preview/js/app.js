/**
 * Find My Work — Main Application Controller & Device Shell Manager
 */

class AppController {
  constructor() {
    this.currentScreen = 'home_dashboard';
    this.audioCtx = null;
    this.init();
  }

  init() {
    // Apply saved theme
    const s = fmwStore.get();
    this.applyTheme(s.theme || 'dark');

    // Subscribe to state updates
    fmwStore.subscribe(state => {
      this.updateBottomNavBadges(state);
      this.updateHeaderSelector(state.currentScreen || this.currentScreen);
    });

    // Start live status bar clock
    this.startClock();

    // Initial render
    this.navigate(s.currentScreen || 'home_dashboard');
  }

  startClock() {
    const updateTime = () => {
      const el = document.getElementById('device_time');
      if (el) {
        const now = new Date();
        el.innerText = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
      }
    };
    updateTime();
    setInterval(updateTime, 1000);
  }

  navigate(screenId, params = null) {
    this.currentScreen = screenId;
    fmwStore.set(s => ({ ...s, currentScreen: screenId }));

    this.renderScreen(screenId, params);
    this.updateBottomNav(screenId);
    this.updateHeaderSelector(screenId);
  }

  renderCurrentScreen() {
    this.renderScreen(this.currentScreen);
  }

  renderScreen(screenId, params = null) {
    const container = document.getElementById('screen_viewport');
    if (!container) return;

    let html = '';
    const screens = window.fmwScreens;

    switch (screenId) {
      case 'splash':
        html = screens.renderSplash();
        break;
      case 'login':
        html = screens.renderLogin();
        break;
      case 'complete_profile':
        html = screens.renderCompleteProfile(params || 1);
        break;
      case 'home_dashboard':
        html = screens.renderHomeDashboard();
        break;
      case 'available_jobs':
        html = screens.renderAvailableJobs();
        break;
      case 'job_details':
        html = screens.renderJobDetails(params);
        break;
      case 'active_job':
        html = screens.renderActiveJob();
        break;
      case 'job_history':
        html = screens.renderJobHistory();
        break;
      case 'earnings':
        html = screens.renderEarnings();
        break;
      case 'profile':
        html = screens.renderProfile();
        break;
      case 'profile_edit':
        html = screens.renderProfileEdit();
        break;
      case 'payment_methods':
        html = screens.renderPaymentMethods();
        break;
      case 'notifications':
        html = screens.renderNotifications();
        break;
      case 'settings':
        html = screens.renderSettings();
        break;
      default:
        html = screens.renderHomeDashboard();
    }

    container.innerHTML = html;
    container.scrollTop = 0;
  }

  updateBottomNav(screenId) {
    const nav = document.getElementById('bottom_nav_bar');
    if (!nav) return;

    const noNavScreens = ['splash', 'login', 'complete_profile'];
    if (noNavScreens.includes(screenId)) {
      nav.classList.add('hidden');
    } else {
      nav.classList.remove('hidden');
    }

    // Active Tab Item
    const items = nav.querySelectorAll('.nav-item');
    items.forEach(el => {
      const target = el.getAttribute('data-screen');
      if (target === screenId || (screenId === 'active_job' && target === 'home_dashboard') || (screenId === 'job_details' && target === 'available_jobs') || (screenId === 'profile_edit' && target === 'profile')) {
        el.classList.add('active');
      } else {
        el.classList.remove('active');
      }
    });

    this.updateBottomNavBadges(fmwStore.get());
  }

  updateBottomNavBadges(state) {
    const badge = document.getElementById('nav_jobs_badge');
    if (badge) {
      const count = state.availableJobs.length;
      if (count > 0) {
        badge.style.display = 'block';
        badge.innerText = count;
      } else {
        badge.style.display = 'none';
      }
    }
  }

  updateHeaderSelector(screenId) {
    const sel = document.getElementById('header_screen_select');
    if (sel && sel.value !== screenId) {
      sel.value = screenId;
    }
  }

  // Device Frame View Mode
  setViewMode(mode) {
    const wrapper = document.getElementById('device_wrapper');
    if (!wrapper) return;

    wrapper.className = 'device-wrapper mode-' + mode;
    this.showToast('Viewport switched to: ' + mode.toUpperCase());
  }

  // Theme Management
  toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') || 'dark';
    const next = current === 'dark' ? 'light' : 'dark';
    this.applyTheme(next);
    fmwStore.set(s => ({ ...s, theme: next }));
    this.renderCurrentScreen();
  }

  applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    const themeBtn = document.getElementById('header_theme_btn');
    if (themeBtn) {
      themeBtn.innerHTML = theme === 'dark' ? '☀️ Light' : '🌙 Dark';
    }
  }

  // Audio Chime Synthesizer
  playChime(type = 'alert') {
    try {
      if (!this.audioCtx) {
        this.audioCtx = new (window.AudioContext || window.webkitAudioContext)();
      }
      if (this.audioCtx.state === 'suspended') {
        this.audioCtx.resume();
      }

      const osc = this.audioCtx.createOscillator();
      const gain = this.audioCtx.createGain();
      osc.connect(gain);
      gain.connect(this.audioCtx.destination);

      if (type === 'alert') {
        osc.frequency.setValueAtTime(587.33, this.audioCtx.currentTime); // D5
        osc.frequency.setValueAtTime(880.00, this.audioCtx.currentTime + 0.1); // A5
        gain.gain.setValueAtTime(0.15, this.audioCtx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.001, this.audioCtx.currentTime + 0.4);
        osc.start();
        osc.stop(this.audioCtx.currentTime + 0.4);
      } else if (type === 'success') {
        osc.frequency.setValueAtTime(523.25, this.audioCtx.currentTime); // C5
        osc.frequency.setValueAtTime(659.25, this.audioCtx.currentTime + 0.1); // E5
        osc.frequency.setValueAtTime(783.99, this.audioCtx.currentTime + 0.2); // G5
        gain.gain.setValueAtTime(0.2, this.audioCtx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.001, this.audioCtx.currentTime + 0.5);
        osc.start();
        osc.stop(this.audioCtx.currentTime + 0.5);
      }
    } catch (e) {
      console.warn('Audio play error:', e);
    }
  }

  // In-App Toast
  showToast(message, type = 'info') {
    const toast = document.getElementById('push_toast');
    const toastMsg = document.getElementById('push_toast_msg');
    if (!toast || !toastMsg) return;

    toastMsg.innerText = message;
    toast.classList.add('show');
    this.playChime(type === 'job' ? 'alert' : 'success');

    if (this._toastTimer) clearTimeout(this._toastTimer);
    this._toastTimer = setTimeout(() => {
      toast.classList.remove('show');
    }, 3500);
  }

  // Interactive Action Handlers
  handleToggleOnline(inputEl) {
    const isOnline = fmwStore.toggleOnline();
    this.showToast(isOnline ? '🟢 You are now ONLINE! Ready for jobs' : '⚪ You are now OFFLINE', isOnline ? 'success' : 'info');
    this.renderCurrentScreen();
  }

  handleAcceptJob(jobId) {
    const job = fmwStore.acceptJob(jobId);
    if (job) {
      this.showToast(`🎉 Accepted job from ${job.customerName}! Opening tracker...`, 'job');
      this.navigate('active_job');
    }
  }

  viewJobDetails(jobId) {
    this.navigate('job_details', jobId);
  }

  // Interactive Modals
  showGoogleAuthModal() {
    const modal = document.getElementById('app_modal');
    const sheet = document.getElementById('app_modal_sheet');
    if (!modal || !sheet) return;

    sheet.innerHTML = `
      <div class="modal-handle"></div>
      <h3 style="font-size: 16px; font-weight: 800; color: var(--text-primary); margin-bottom: 4px; text-align: center;">Choose a Google Account</h3>
      <p style="font-size: 12px; color: var(--text-secondary); text-align: center; margin-bottom: 20px;">to continue to Find My Work Partner</p>

      <div style="display: flex; flex-direction: column; gap: 10px;">
        <div style="display: flex; align-items: center; gap: 12px; padding: 12px; border-radius: 12px; background: var(--bg-card); border: 1px solid var(--border-color); cursor: pointer;" onclick="fmwApp.closeModal(); fmwApp.navigate('home_dashboard'); fmwApp.showToast('✅ Signed in as Rajesh Varmora');">
          <div style="width: 36px; height: 36px; border-radius: 50%; background: #1a3c8f; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700;">R</div>
          <div style="flex: 1;">
            <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">Rajesh Varmora</div>
            <div style="font-size: 11px; color: var(--text-secondary);">rajesh.varmora@example.com</div>
          </div>
        </div>

        <div style="display: flex; align-items: center; gap: 12px; padding: 12px; border-radius: 12px; background: var(--bg-card); border: 1px solid var(--border-color); cursor: pointer;" onclick="fmwApp.closeModal(); fmwApp.navigate('complete_profile', 1);">
          <div style="width: 36px; height: 36px; border-radius: 50%; background: #374151; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700;">+</div>
          <div style="flex: 1;">
            <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">Use another Google account</div>
            <div style="font-size: 11px; color: var(--text-secondary);">Register as a new service worker</div>
          </div>
        </div>
      </div>

      <button class="btn-app btn-app-outline" style="width: 100%; margin-top: 20px;" onclick="fmwApp.closeModal()">
        Cancel
      </button>
    `;

    modal.classList.add('active');
  }

  showOtpModal(otp) {
    const modal = document.getElementById('app_modal');
    const sheet = document.getElementById('app_modal_sheet');
    if (!modal || !sheet) return;

    sheet.innerHTML = `
      <div class="modal-handle"></div>
      <h3 style="font-size: 16px; font-weight: 800; color: var(--text-primary); text-align: center;">Verify Customer Start OTP</h3>
      <p style="font-size: 12px; color: var(--text-secondary); text-align: center; margin-top: 4px; margin-bottom: 16px;">
        Ask the customer for the 4-digit start OTP shown on their screen.
      </p>

      <div style="display: flex; justify-content: center; gap: 10px; margin-bottom: 20px;">
        <input type="text" id="otp_in" class="form-input" style="width: 160px; text-align: center; font-size: 22px; font-weight: 800; letter-spacing: 6px;" value="${otp}" maxlength="4">
      </div>

      <button class="btn-app btn-app-primary" style="width: 100%; padding: 14px; font-size: 14px;" onclick="fmwStore.updateActiveJobStatus('STARTED'); fmwApp.closeModal(); fmwApp.showToast('🚀 OTP Verified! Work in progress timer started.'); fmwApp.renderCurrentScreen();">
        Verify OTP & Begin Work ➔
      </button>
    `;

    modal.classList.add('active');
  }

  showPaymentCompleteModal(jobId, amount) {
    const modal = document.getElementById('app_modal');
    const sheet = document.getElementById('app_modal_sheet');
    if (!modal || !sheet) return;

    sheet.innerHTML = `
      <div class="modal-handle"></div>
      <div style="text-align: center; margin-bottom: 16px;">
        <div style="font-size: 40px; margin-bottom: 4px;">💰</div>
        <h3 style="font-size: 18px; font-weight: 800; color: var(--text-primary);">Collect ${window.formatInr(amount)}</h3>
        <p style="font-size: 12px; color: var(--text-secondary);">Choose how the customer paid you</p>
      </div>

      <div style="display: flex; flex-direction: column; gap: 10px; margin-bottom: 18px;">
        <div style="display: flex; align-items: center; justify-content: space-between; padding: 12px; background: var(--bg-card); border-radius: 12px; border: 1px solid var(--border-color); cursor: pointer;" onclick="fmwScreens.finishPaymentAndRate('UPI', ${amount})">
          <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 20px;">⚡</span>
            <div>
              <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">UPI / Online Transfer</div>
              <div style="font-size: 11px; color: var(--text-secondary);">Direct to bank / QR scan</div>
            </div>
          </div>
          <span class="status-chip completed">Recommended</span>
        </div>

        <div style="display: flex; align-items: center; justify-content: space-between; padding: 12px; background: var(--bg-card); border-radius: 12px; border: 1px solid var(--border-color); cursor: pointer;" onclick="fmwScreens.finishPaymentAndRate('Cash', ${amount})">
          <div style="display: flex; align-items: center; gap: 10px;">
            <span style="font-size: 20px;">💵</span>
            <div>
              <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">Cash Payment Collected</div>
              <div style="font-size: 11px; color: var(--text-secondary);">Received ${window.formatInr(amount)} in cash</div>
            </div>
          </div>
        </div>
      </div>

      <button class="btn-app btn-app-outline" style="width: 100%;" onclick="fmwApp.closeModal()">
        Cancel
      </button>
    `;

    modal.classList.add('active');
  }

  showWithdrawModal(amount) {
    const modal = document.getElementById('app_modal');
    const sheet = document.getElementById('app_modal_sheet');
    if (!modal || !sheet) return;

    sheet.innerHTML = `
      <div class="modal-handle"></div>
      <div style="text-align: center; margin-bottom: 16px;">
        <div style="font-size: 36px; margin-bottom: 4px;">⚡</div>
        <h3 style="font-size: 18px; font-weight: 800; color: var(--text-primary);">Instant UPI Payout</h3>
        <p style="font-size: 12px; color: var(--text-secondary);">Transfer ${window.formatInr(amount)} directly to your linked UPI</p>
      </div>

      <div class="fmw-card" style="margin-bottom: 16px;">
        <div style="font-size: 11px; color: var(--text-secondary); text-transform: uppercase; font-weight: 600;">Destination</div>
        <div style="font-size: 14px; font-weight: 700; color: var(--text-primary); margin-top: 2px;">rajesh.varmora@oksbi</div>
        <div style="font-size: 11px; color: var(--fmw-success); margin-top: 4px;">Zero withdrawal fee • Instant credit</div>
      </div>

      <button class="btn-app btn-app-primary" style="width: 100%; padding: 14px; font-size: 14px;" onclick="fmwApp.closeModal(); fmwApp.showToast('✅ ${window.formatInr(amount)} sent to your UPI successfully!', 'success');">
        Confirm Withdrawal ➔
      </button>
    `;

    modal.classList.add('active');
  }

  closeModal() {
    const modal = document.getElementById('app_modal');
    if (modal) {
      modal.classList.remove('active');
    }
  }
}

// Payment Finish Helper
window.fmwScreens.finishPaymentAndRate = function(method, amount) {
  fmwStore.updateActiveJobStatus('COMPLETED');
  fmwApp.closeModal();
  fmwApp.showToast(`🎉 Work Completed! ${window.formatInr(amount)} recorded via ${method}.`, 'success');
  fmwApp.navigate('earnings');
};

// Initialize App globally
window.fmwApp = new AppController();
