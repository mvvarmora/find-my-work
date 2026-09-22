/**
 * Find My Work — Screen Renderers & Interactive UI Handlers
 */

window.fmwScreens = {
  // 1. SPLASH SCREEN
  renderSplash() {
    return `
      <div class="splash-container">
        <img src="assets/app_logo.png" alt="Find My Work" class="splash-logo-pulse" onerror="this.src='https://cdn-icons-png.flaticon.com/512/3135/3135715.png'">
        <h2 style="font-size: 26px; font-weight: 800; color: #fff; margin-bottom: 6px; letter-spacing: -0.5px;">Find My Work</h2>
        <p style="font-size: 13px; color: #93c5fd; max-width: 260px; line-height: 1.5; margin-bottom: 32px;">
          Empowering Local Skilled Workers with Instant Jobs & Fast Payouts
        </p>
        <div style="display: flex; flex-direction: column; width: 100%; gap: 12px; max-width: 280px;">
          <button class="btn-app btn-app-primary" style="padding: 14px; font-size: 15px; border-radius: 14px; background: linear-gradient(135deg, #1A3C8F, #2563eb); box-shadow: 0 8px 25px rgba(26, 60, 143, 0.5);" onclick="fmwApp.navigate('home_dashboard')">
            Enter Dashboard ➔
          </button>
          <button class="btn-app btn-app-outline" style="padding: 12px; font-size: 13px; border-radius: 12px; color: #94a3b8; border-color: rgba(255,255,255,0.15);" onclick="fmwApp.navigate('login')">
            Sign In with Google
          </button>
          <button class="btn-app btn-app-outline" style="padding: 12px; font-size: 13px; border-radius: 12px; color: #ffb800; border-color: rgba(255,184,0,0.3);" onclick="fmwApp.navigate('complete_profile')">
            5-Step Onboarding Flow
          </button>
        </div>
      </div>
    `;
  },

  // 2. LOGIN SCREEN
  renderLogin() {
    return `
      <div style="padding: 32px 20px; display: flex; flex-direction: column; min-height: 100%; justify-content: space-between;">
        <div style="display: flex; flex-direction: column; align-items: center; text-align: center; margin-top: 24px;">
          <img src="assets/app_logo.png" alt="Logo" style="width: 80px; height: 80px; border-radius: 20px; box-shadow: 0 10px 25px rgba(26, 60, 143, 0.3); margin-bottom: 20px;">
          <div class="status-chip accepted" style="margin-bottom: 12px;">Worker Partner App</div>
          <h2 style="font-size: 24px; font-weight: 800; color: var(--text-primary); margin-bottom: 8px;">Welcome Back!</h2>
          <p style="font-size: 13px; color: var(--text-secondary); max-width: 260px; line-height: 1.4;">
            Log in to manage jobs, receive live bookings, and track your daily earnings.
          </p>
        </div>

        <div style="display: flex; flex-direction: column; gap: 14px; margin: 36px 0;">
          <button onclick="fmwApp.showGoogleAuthModal()" style="display: flex; align-items: center; justify-content: center; gap: 12px; width: 100%; padding: 14px; background: #ffffff; color: #1f2937; border-radius: 14px; border: 1px solid #e5e7eb; font-weight: 700; font-size: 14px; cursor: pointer; box-shadow: 0 4px 14px rgba(0,0,0,0.1); transition: transform 0.2s;">
            <svg width="20" height="20" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/>
            </svg>
            Continue with Google
          </button>

          <button onclick="fmwApp.navigate('home_dashboard')" class="btn-app btn-app-outline" style="border-radius: 14px; padding: 13px;">
            Demo Guest Login
          </button>
        </div>

        <div style="text-align: center; font-size: 11px; color: var(--text-tertiary); line-height: 1.5;">
          By signing in, you agree to Find My Work's <br>
          <span style="color: #82a8ff; text-decoration: underline; cursor: pointer;">Terms of Service</span> & <span style="color: #82a8ff; text-decoration: underline; cursor: pointer;">Privacy Policy</span>
        </div>
      </div>
    `;
  },

  // 3. COMPLETE PROFILE (5-STEP STEPPER)
  renderCompleteProfile(step = 1) {
    const s = fmwStore.get();
    const w = s.worker;

    return `
      <div class="screen-content" style="padding-bottom: 24px;">
        <div style="display: flex; align-items: center; justify-content: space-between; margin-top: 4px;">
          <div>
            <h2 style="font-size: 18px; font-weight: 800; color: var(--text-primary);">Worker Onboarding</h2>
            <p style="font-size: 12px; color: var(--text-secondary);">Step ${step} of 5 — Complete your profile</p>
          </div>
          <span class="status-chip ${step === 5 ? 'completed' : 'pending'}">${Math.round((step / 5) * 100)}% Done</span>
        </div>

        <div class="stepper-progress-bar">
          <div class="stepper-progress-fill" style="width: ${(step / 5) * 100}%;"></div>
        </div>

        ${step === 1 ? `
          <!-- STEP 1: Personal Info -->
          <div class="fmw-card">
            <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 12px;">1. Personal Details</h3>
            <div class="form-group">
              <label class="form-label">Full Name</label>
              <input type="text" id="ob_name" class="form-input" value="${w.name}" placeholder="e.g. Rajesh Varmora">
            </div>
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">Age</label>
                <input type="number" id="ob_age" class="form-input" value="${w.age || 30}">
              </div>
              <div class="form-group">
                <label class="form-label">Gender</label>
                <select id="ob_gender" class="form-select">
                  <option value="Male" ${w.gender === 'Male' ? 'selected' : ''}>Male</option>
                  <option value="Female" ${w.gender === 'Female' ? 'selected' : ''}>Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">Service City</label>
              <input type="text" id="ob_city" class="form-input" value="${w.city}" placeholder="e.g. Ahmedabad">
            </div>
            <div class="form-group">
              <label class="form-label">Short Professional Bio</label>
              <textarea id="ob_bio" class="form-textarea" rows="3" placeholder="Describe your experience and specialties...">${w.bio}</textarea>
            </div>
            <button class="btn-app btn-app-primary" style="margin-top: 8px;" onclick="fmwScreens.saveOnboardingStep(1)">
              Next: Select Skills & Categories ➔
            </button>
          </div>
        ` : ''}

        ${step === 2 ? `
          <!-- STEP 2: Categories & Skills -->
          <div class="fmw-card">
            <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 6px;">2. Category & Skills</h3>
            <p style="font-size: 12px; color: var(--text-secondary); margin-bottom: 14px;">Select the primary trade categories you provide:</p>
            <div class="chips-cloud" id="ob_category_chips">
              ${s.categoriesList.map(c => `
                <div class="chip-select ${w.categoryIds.includes(c.id) ? 'selected' : ''}" onclick="this.classList.toggle('selected')">
                  <span>${c.icon}</span> ${c.name}
                </div>
              `).join('')}
            </div>

            <div class="form-group" style="margin-top: 18px;">
              <label class="form-label">Key Specialties / Tags</label>
              <input type="text" id="ob_skills" class="form-input" value="${w.skills.join(', ')}" placeholder="Wiring, MCB, Inverter, Drill...">
              <small style="color: var(--text-tertiary); font-size: 11px;">Separate tags with commas</small>
            </div>

            <div style="display: flex; gap: 10px; margin-top: 14px;">
              <button class="btn-app btn-app-outline" onclick="fmwApp.renderScreen('complete_profile', 1)">Back</button>
              <button class="btn-app btn-app-primary" onclick="fmwScreens.saveOnboardingStep(2)">Next: Rates & Radius ➔</button>
            </div>
          </div>
        ` : ''}

        ${step === 3 ? `
          <!-- STEP 3: Rates & Radius -->
          <div class="fmw-card">
            <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 12px;">3. Experience & Rates</h3>
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">Experience (Years)</label>
                <input type="number" id="ob_exp" class="form-input" value="${w.experienceYears}">
              </div>
              <div class="form-group">
                <label class="form-label">Work Radius (km)</label>
                <input type="number" id="ob_radius" class="form-input" value="${w.workingRadiusKm}">
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">Hourly Rate (₹ INR)</label>
              <input type="number" id="ob_hourly" class="form-input" value="${w.hourlyRate}">
            </div>
            <div class="form-group">
              <label class="form-label">Daily Full-Day Rate (₹ INR)</label>
              <input type="number" id="ob_daily" class="form-input" value="${w.dailyRate}">
            </div>
            <div style="display: flex; align-items: center; justify-content: space-between; padding: 10px 0;">
              <div>
                <div style="font-size: 13px; font-weight: 600; color: var(--text-primary);">Emergency Night Service</div>
                <div style="font-size: 11px; color: var(--text-secondary);">Available after 9 PM for urgent calls</div>
              </div>
              <label class="toggle-switch">
                <input type="checkbox" id="ob_after_hours" ${w.availableAfterHours ? 'checked' : ''}>
                <span class="toggle-slider"></span>
              </label>
            </div>
            <div style="display: flex; gap: 10px; margin-top: 14px;">
              <button class="btn-app btn-app-outline" onclick="fmwApp.renderScreen('complete_profile', 2)">Back</button>
              <button class="btn-app btn-app-primary" onclick="fmwScreens.saveOnboardingStep(3)">Next: KYC & Aadhaar ➔</button>
            </div>
          </div>
        ` : ''}

        ${step === 4 ? `
          <!-- STEP 4: Aadhaar & KYC -->
          <div class="fmw-card">
            <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 6px;">4. Aadhaar Verification (KYC)</h3>
            <p style="font-size: 12px; color: var(--text-secondary); margin-bottom: 14px;">
              Government ID verification guarantees instant customer trust and Verified badge.
            </p>

            <div style="border: 2px dashed var(--border-color); border-radius: 14px; padding: 24px 16px; text-align: center; background: var(--bg-input); margin-bottom: 16px;">
              <div style="font-size: 32px; margin-bottom: 8px;">🪪</div>
              <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">Aadhaar Card Uploaded</div>
              <div style="font-size: 11px; color: var(--fmw-success); font-weight: 600; margin-top: 4px;">✅ Verified via DigiLocker OCR</div>
            </div>

            <div class="form-group">
              <label class="form-label">Aadhaar Number (Last 4 Digits)</label>
              <input type="text" class="form-input" value="XXXX-XXXX-8921" disabled style="opacity: 0.7;">
            </div>

            <div style="display: flex; gap: 10px; margin-top: 14px;">
              <button class="btn-app btn-app-outline" onclick="fmwApp.renderScreen('complete_profile', 3)">Back</button>
              <button class="btn-app btn-app-primary" onclick="fmwScreens.saveOnboardingStep(4)">Next: Bank & UPI Info ➔</button>
            </div>
          </div>
        ` : ''}

        ${step === 5 ? `
          <!-- STEP 5: Bank & UPI -->
          <div class="fmw-card">
            <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 6px;">5. Bank & UPI Payouts</h3>
            <p style="font-size: 12px; color: var(--text-secondary); margin-bottom: 14px;">
              Where should we send your daily customer earnings?
            </p>

            <div class="form-group">
              <label class="form-label">UPI ID (Instant Settlement)</label>
              <input type="text" id="ob_upi" class="form-input" value="${w.upiId}" placeholder="e.g. yourname@oksbi">
            </div>

            <div class="form-group">
              <label class="form-label">Bank Account Holder Name</label>
              <input type="text" id="ob_holder" class="form-input" value="${w.bankHolderName}">
            </div>

            <div class="form-group">
              <label class="form-label">Bank Account Number</label>
              <input type="password" id="ob_acc" class="form-input" value="${w.bankAccountNumber}">
            </div>

            <div class="form-group">
              <label class="form-label">Bank IFSC Code</label>
              <input type="text" id="ob_ifsc" class="form-input" value="${w.bankIfsc}">
            </div>

            <div style="display: flex; gap: 10px; margin-top: 14px;">
              <button class="btn-app btn-app-outline" onclick="fmwApp.renderScreen('complete_profile', 4)">Back</button>
              <button class="btn-app btn-app-success" onclick="fmwScreens.finishOnboarding()">
                🎉 Complete & Launch App
              </button>
            </div>
          </div>
        ` : ''}
      </div>
    `;
  },

  saveOnboardingStep(step) {
    const s = fmwStore.get();
    if (step === 1) {
      const name = document.getElementById('ob_name')?.value || s.worker.name;
      const age = parseInt(document.getElementById('ob_age')?.value || '30', 10);
      const gender = document.getElementById('ob_gender')?.value || 'Male';
      const city = document.getElementById('ob_city')?.value || s.worker.city;
      const bio = document.getElementById('ob_bio')?.value || s.worker.bio;
      fmwStore.set(st => ({
        ...st,
        worker: { ...st.worker, name, age, gender, city, bio }
      }));
      fmwApp.renderScreen('complete_profile', 2);
    } else if (step === 2) {
      const skillsStr = document.getElementById('ob_skills')?.value || '';
      const skills = skillsStr.split(',').map(x => x.trim()).filter(Boolean);
      fmwStore.set(st => ({
        ...st,
        worker: { ...st.worker, skills }
      }));
      fmwApp.renderScreen('complete_profile', 3);
    } else if (step === 3) {
      const exp = parseInt(document.getElementById('ob_exp')?.value || '5', 10);
      const radius = parseInt(document.getElementById('ob_radius')?.value || '15', 10);
      const hourly = parseFloat(document.getElementById('ob_hourly')?.value || '299');
      const daily = parseFloat(document.getElementById('ob_daily')?.value || '1800');
      const afterHours = document.getElementById('ob_after_hours')?.checked || false;
      fmwStore.set(st => ({
        ...st,
        worker: { ...st.worker, experienceYears: exp, workingRadiusKm: radius, hourlyRate: hourly, dailyRate: daily, availableAfterHours: afterHours }
      }));
      fmwApp.renderScreen('complete_profile', 4);
    } else if (step === 4) {
      fmwApp.renderScreen('complete_profile', 5);
    }
  },

  finishOnboarding() {
    const upi = document.getElementById('ob_upi')?.value || 'rajesh@oksbi';
    const holder = document.getElementById('ob_holder')?.value || 'Rajesh Varmora';
    const acc = document.getElementById('ob_acc')?.value || '918273645012';
    const ifsc = document.getElementById('ob_ifsc')?.value || 'SBIN0001824';

    fmwStore.set(st => ({
      ...st,
      hasCompletedProfile: true,
      worker: {
        ...st.worker,
        upiId: upi,
        bankHolderName: holder,
        bankAccountNumber: acc,
        bankIfsc: ifsc,
        documentsVerified: true
      }
    }));

    fmwApp.showToast('🎉 Profile completed successfully! Welcome to Find My Work.');
    fmwApp.navigate('home_dashboard');
  },

  // 4. HOME DASHBOARD SCREEN
  renderHomeDashboard() {
    const s = fmwStore.get();
    const w = s.worker;
    const active = s.activeJob;
    const todayEarnings = s.earnings
      .filter(e => e.date >= (Date.now() - 24 * 3600 * 1000))
      .reduce((sum, item) => sum + item.amount, 0);

    return `
      <!-- Top App Bar -->
      <div class="app-top-bar">
        <div class="title-group" onclick="fmwApp.navigate('profile')" style="cursor: pointer;">
          <div class="avatar-sm">
            <img src="${w.profilePhotoUrl}" alt="${w.name}" onerror="this.src='https://cdn-icons-png.flaticon.com/512/3135/3135715.png'">
          </div>
          <div class="worker-meta">
            <h3>${w.name}</h3>
            <p>${w.city} • <span style="color: #ffb800;">★ ${w.rating}</span></p>
          </div>
        </div>

        <div style="display: flex; align-items: center; gap: 8px;">
          <div class="online-toggle-wrap">
            <div class="pulse-dot ${w.isOnline ? 'online' : ''}"></div>
            <span style="font-size: 11px; font-weight: 700; color: ${w.isOnline ? 'var(--fmw-success)' : 'var(--text-secondary)'};">
              ${w.isOnline ? 'ONLINE' : 'OFFLINE'}
            </span>
            <label class="toggle-switch">
              <input type="checkbox" id="home_online_toggle" ${w.isOnline ? 'checked' : ''} onchange="fmwApp.handleToggleOnline(this)">
              <span class="toggle-slider"></span>
            </label>
          </div>

          <div onclick="fmwApp.navigate('notifications')" style="position: relative; width: 36px; height: 36px; border-radius: 10px; background: var(--bg-card); display: flex; align-items: center; justify-content: center; cursor: pointer; border: 1px solid var(--border-color);">
            <svg width="18" height="18" fill="var(--text-primary)" viewBox="0 0 24 24"><path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"/></svg>
            ${s.notifications.some(n => !n.read) ? `<span style="position: absolute; top: 6px; right: 6px; width: 8px; height: 8px; border-radius: 50%; background: #ef4444;"></span>` : ''}
          </div>
        </div>
      </div>

      <div class="screen-content">
        <!-- Today's Earnings Card -->
        <div class="earnings-hero-card">
          <div class="label">Today's Earnings</div>
          <div class="amount">${window.formatInr(todayEarnings)}</div>
          <div class="hero-footer">
            <div class="hero-pill">
              <span>📈 +24% vs yesterday</span>
            </div>
            <button onclick="fmwApp.navigate('earnings')" style="background: rgba(255,255,255,0.25); border: none; color: #fff; padding: 6px 12px; border-radius: 10px; font-size: 12px; font-weight: 700; cursor: pointer;">
              Breakdown ➔
            </button>
          </div>
        </div>

        <!-- Active Job Banner (if any) -->
        ${active ? `
          <div class="fmw-card" style="border-left: 4px solid var(--fmw-amber); background: linear-gradient(135deg, var(--bg-card) 0%, rgba(255, 184, 0, 0.05) 100%);">
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
              <span class="status-chip ${active.status.toLowerCase()}">Active • ${active.status.replace(/_/g, ' ')}</span>
              <span style="font-size: 14px; font-weight: 800; color: var(--fmw-success);">${window.formatInr(active.totalAmount)}</span>
            </div>
            <h4 style="font-size: 15px; font-weight: 700; color: var(--text-primary); margin-bottom: 4px;">${active.subServiceName}</h4>
            <p style="font-size: 12px; color: var(--text-secondary); margin-bottom: 12px;">👤 ${active.customerName} • 📍 ${active.societyName || active.city}</p>
            <button class="btn-app btn-app-primary" style="width: 100%;" onclick="fmwApp.navigate('active_job')">
              Open Active Job Tracker ➔
            </button>
          </div>
        ` : `
          <div class="fmw-card" style="display: flex; align-items: center; justify-content: space-between; padding: 14px;">
            <div>
              <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">Ready for New Jobs</div>
              <div style="font-size: 11px; color: var(--text-secondary);">${s.availableJobs.length} nearby requests waiting</div>
            </div>
            <button class="btn-app btn-app-primary" style="flex: 0 0 auto; padding: 8px 14px; font-size: 12px;" onclick="fmwApp.navigate('available_jobs')">
              Browse Jobs (${s.availableJobs.length})
            </button>
          </div>
        `}

        <!-- Performance Stats (4 Cards) -->
        <div class="section-title">
          <span>Performance Overview</span>
        </div>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-icon" style="background: rgba(59, 130, 246, 0.15); color: #60a5fa;">💼</div>
            <div class="stat-val">${w.totalJobs}</div>
            <div class="stat-lbl">Jobs Completed</div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: rgba(255, 184, 0, 0.15); color: #ffb800;">⭐</div>
            <div class="stat-val">${w.rating}</div>
            <div class="stat-lbl">Avg Rating (${w.ratingCount})</div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: rgba(34, 197, 94, 0.15); color: #4ade80;">🎯</div>
            <div class="stat-val">${w.completionRate}%</div>
            <div class="stat-lbl">Completion Rate</div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: rgba(168, 85, 247, 0.15); color: #c084fc;">💰</div>
            <div class="stat-val">${window.formatInr(w.totalEarnings)}</div>
            <div class="stat-lbl">Lifetime Earnings</div>
          </div>
        </div>

        <!-- Recent Notifications Section -->
        <div class="section-title">
          <span>Recent Activity</span>
          <a onclick="fmwApp.navigate('notifications')">View All</a>
        </div>
        <div style="display: flex; flex-direction: column; gap: 8px;">
          ${s.notifications.slice(0, 2).map(n => `
            <div class="fmw-card" style="padding: 12px; display: flex; align-items: flex-start; gap: 10px; cursor: pointer;" onclick="fmwApp.navigate('${n.targetScreen || 'notifications'}')">
              <div style="font-size: 18px;">${n.type === 'JOB' ? '⚡' : n.type === 'PAYMENT' ? '💰' : '⭐'}</div>
              <div style="flex: 1;">
                <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">${n.title}</div>
                <div style="font-size: 11px; color: var(--text-secondary);">${n.message}</div>
              </div>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  },

  // 5. AVAILABLE JOBS SCREEN
  renderAvailableJobs() {
    const s = fmwStore.get();
    const jobs = s.availableJobs;

    return `
      <div class="app-top-bar">
        <div>
          <h2 style="font-size: 16px; font-weight: 800; color: var(--text-primary);">Available Jobs</h2>
          <p style="font-size: 11px; color: var(--text-secondary);">${jobs.length} customers requesting service near you</p>
        </div>
        <button class="btn-ctrl btn-amber" style="padding: 6px 10px; font-size: 11px;" onclick="fmwStore.simulateIncomingJob(); fmwApp.showToast('⚡ New incoming job simulated!'); fmwApp.renderCurrentScreen();">
          + Test Alert
        </button>
      </div>

      <div class="screen-content">
        <!-- Search & Filter Tabs -->
        <div class="chips-cloud" style="margin-bottom: 4px;">
          <div class="chip-select selected">All (${jobs.length})</div>
          <div class="chip-select">⚡ Emergency</div>
          <div class="chip-select">&lt; 5 km</div>
          <div class="chip-select">High Pay</div>
        </div>

        ${jobs.length === 0 ? `
          <div style="text-align: center; padding: 48px 16px;">
            <div style="font-size: 48px; margin-bottom: 12px;">🎉</div>
            <h3 style="font-size: 16px; font-weight: 700; color: var(--text-primary);">No Pending Requests</h3>
            <p style="font-size: 12px; color: var(--text-secondary); margin-top: 4px; margin-bottom: 16px;">
              You have accepted all available jobs or no new requests in your area right now.
            </p>
            <button class="btn-app btn-app-primary" onclick="fmwStore.simulateIncomingJob(); fmwApp.renderCurrentScreen();">
              Simulate New Customer Request
            </button>
          </div>
        ` : jobs.map(j => `
          <div class="job-card" onclick="fmwApp.viewJobDetails('${j.id}')">
            <div class="job-card-header">
              <div class="customer-info">
                <div class="cust-avatar">${j.customerName[0]}</div>
                <div>
                  <h4 class="job-title">${j.subServiceName}</h4>
                  <p class="job-sub">👤 ${j.customerName} • ${j.postedTimeAgo}</p>
                </div>
              </div>
              <div class="job-price">${window.formatInr(j.totalAmount)}</div>
            </div>

            <div class="job-meta-row">
              <div class="job-meta-item">
                <svg viewBox="0 0 24 24"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/></svg>
                <span>${j.distanceKm} km away • ${j.societyName || j.city}</span>
              </div>
              ${j.isEmergency ? `
                <span class="status-chip pending" style="font-size: 10px;">⚡ High Priority</span>
              ` : ''}
            </div>

            <p style="font-size: 12px; color: var(--text-secondary); line-height: 1.4; background: var(--bg-card-subtle); padding: 8px 10px; border-radius: 8px;">
              "${j.specialInstructions}"
            </p>

            <div class="job-actions" onclick="event.stopPropagation();">
              <button class="btn-app btn-app-outline" onclick="fmwApp.viewJobDetails('${j.id}')">
                Details
              </button>
              <button class="btn-app btn-app-primary" onclick="fmwApp.handleAcceptJob('${j.id}')">
                Accept Job ➔
              </button>
            </div>
          </div>
        `).join('')}
      </div>
    `;
  },

  // 6. JOB DETAILS SCREEN
  renderJobDetails(jobId) {
    const s = fmwStore.get();
    const job = s.availableJobs.find(j => j.id === jobId) || s.activeJob;

    if (!job) {
      return `
        <div class="screen-content" style="text-align: center; padding-top: 48px;">
          <p>Job not found.</p>
          <button class="btn-app btn-app-outline" onclick="fmwApp.navigate('available_jobs')">Back to Jobs</button>
        </div>
      `;
    }

    return `
      <div class="app-top-bar">
        <div style="display: flex; align-items: center; gap: 8px;">
          <button class="btn-app btn-app-outline" style="padding: 6px 10px; font-size: 12px;" onclick="fmwApp.navigate('available_jobs')">
            ← Back
          </button>
          <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary);">Job Details</h3>
        </div>
        <span class="status-chip ${job.status.toLowerCase()}">${job.status}</span>
      </div>

      <div class="screen-content">
        <!-- Customer Card -->
        <div class="fmw-card">
          <div style="display: flex; align-items: center; justify-content: space-between;">
            <div style="display: flex; align-items: center; gap: 12px;">
              <div class="cust-avatar" style="width: 48px; height: 48px; font-size: 18px;">${job.customerName[0]}</div>
              <div>
                <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary);">${job.customerName}</h3>
                <p style="font-size: 12px; color: var(--text-secondary);">Customer • Verified</p>
              </div>
            </div>
            <button onclick="fmwApp.showToast('📞 Calling customer: ' + '${job.customerPhone}')" style="width: 38px; height: 38px; border-radius: 50%; background: rgba(34, 197, 94, 0.15); color: var(--fmw-success); border: 1px solid rgba(34, 197, 94, 0.3); display: flex; align-items: center; justify-content: center; cursor: pointer;">
              📞
            </button>
          </div>
        </div>

        <!-- Service & Address -->
        <div class="fmw-card">
          <div style="margin-bottom: 12px;">
            <span style="font-size: 11px; color: var(--text-tertiary); font-weight: 600; text-transform: uppercase;">Service Requested</span>
            <h4 style="font-size: 16px; font-weight: 700; color: var(--text-primary); margin-top: 2px;">${job.subServiceName}</h4>
          </div>

          <div style="margin-bottom: 12px;">
            <span style="font-size: 11px; color: var(--text-tertiary); font-weight: 600; text-transform: uppercase;">Special Instructions</span>
            <p style="font-size: 13px; color: var(--text-secondary); margin-top: 2px;">${job.specialInstructions || 'None provided'}</p>
          </div>

          <div style="margin-bottom: 12px;">
            <span style="font-size: 11px; color: var(--text-tertiary); font-weight: 600; text-transform: uppercase;">Location Address</span>
            <p style="font-size: 13px; color: var(--text-primary); font-weight: 600; margin-top: 2px;">
              ${job.flatNo ? job.flatNo + ', ' : ''}${job.societyName ? job.societyName + ', ' : ''}${job.landmark ? job.landmark + ', ' : ''}${job.city}
            </p>
          </div>

          <!-- Map Simulation Preview -->
          <div style="height: 110px; border-radius: 12px; background: linear-gradient(135deg, #1e293b, #0f172a); border: 1px solid var(--border-color); display: flex; align-items: center; justify-content: center; position: relative; overflow: hidden; margin-top: 10px;">
            <div style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; opacity: 0.25; background-image: radial-gradient(#60a5fa 1px, transparent 1px); background-size: 16px 16px;"></div>
            <div style="display: flex; align-items: center; gap: 8px; z-index: 2; background: rgba(0,0,0,0.6); backdrop-filter: blur(6px); padding: 6px 14px; border-radius: 99px;">
              <span>📍</span>
              <span style="font-size: 12px; font-weight: 700; color: #fff;">${job.distanceKm} km away • Open in Maps</span>
            </div>
          </div>
        </div>

        <!-- Pricing Breakdown -->
        <div class="fmw-card">
          <h4 style="font-size: 14px; font-weight: 700; color: var(--text-primary); margin-bottom: 10px;">Earnings Breakdown</h4>
          <div style="display: flex; justify-content: space-between; font-size: 13px; color: var(--text-secondary); margin-bottom: 6px;">
            <span>Service Base Charge</span>
            <span>${window.formatInr(job.totalAmount * 0.85)}</span>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 13px; color: var(--text-secondary); margin-bottom: 6px;">
            <span>Travel Allowance</span>
            <span>${window.formatInr(job.totalAmount * 0.15)}</span>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 15px; font-weight: 800; color: var(--fmw-success); margin-top: 8px; padding-top: 8px; border-top: 1px solid var(--border-color);">
            <span>Total You Earn</span>
            <span>${window.formatInr(job.totalAmount)}</span>
          </div>
        </div>

        <!-- Primary Action -->
        ${job.status === 'PENDING' ? `
          <button class="btn-app btn-app-primary" style="padding: 14px; font-size: 15px;" onclick="fmwApp.handleAcceptJob('${job.id}')">
            Accept & Start Job ➔
          </button>
        ` : `
          <button class="btn-app btn-app-primary" style="padding: 14px; font-size: 15px;" onclick="fmwApp.navigate('active_job')">
            Go to Active Job Timeline ➔
          </button>
        `}
      </div>
    `;
  },

  // 7. ACTIVE JOB SCREEN (5-STAGE TIMELINE SIMULATOR)
  renderActiveJob() {
    const s = fmwStore.get();
    const job = s.activeJob;

    if (!job) {
      return `
        <div class="screen-content" style="text-align: center; padding: 48px 16px;">
          <div style="font-size: 48px; margin-bottom: 12px;">✅</div>
          <h3 style="font-size: 18px; font-weight: 800; color: var(--text-primary);">No Active Job Running</h3>
          <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px; margin-bottom: 20px;">
            Accept a job from the Available Jobs feed to track live execution.
          </p>
          <button class="btn-app btn-app-primary" onclick="fmwApp.navigate('available_jobs')">
            Find Available Jobs
          </button>
        </div>
      `;
    }

    const stages = ['ACCEPTED', 'ON_THE_WAY', 'ARRIVED', 'STARTED', 'COMPLETED'];
    const currentIdx = stages.indexOf(job.status);

    return `
      <div class="app-top-bar">
        <div>
          <h2 style="font-size: 16px; font-weight: 800; color: var(--text-primary);">Active Job Tracker</h2>
          <p style="font-size: 11px; color: var(--text-secondary);">Real-time customer synchronization</p>
        </div>
        <span class="status-chip ${job.status.toLowerCase()}">${job.status.replace(/_/g, ' ')}</span>
      </div>

      <div class="screen-content">
        <!-- Customer Info Banner -->
        <div class="fmw-card" style="background: linear-gradient(135deg, var(--bg-card) 0%, rgba(26, 60, 143, 0.1) 100%);">
          <div style="display: flex; align-items: center; justify-content: space-between;">
            <div>
              <h3 style="font-size: 16px; font-weight: 800; color: var(--text-primary);">${job.subServiceName}</h3>
              <p style="font-size: 12px; color: var(--text-secondary); margin-top: 2px;">
                👤 ${job.customerName} • 📍 ${job.societyName || job.city}
              </p>
            </div>
            <div style="font-size: 18px; font-weight: 800; color: var(--fmw-success);">
              ${window.formatInr(job.totalAmount)}
            </div>
          </div>

          <div style="display: flex; gap: 8px; margin-top: 14px;">
            <button class="btn-app btn-app-outline" style="padding: 8px;" onclick="fmwApp.showToast('📞 Calling ${job.customerPhone}')">
              📞 Call Customer
            </button>
            <button class="btn-app btn-app-outline" style="padding: 8px;" onclick="fmwApp.showToast('🗺️ Opening Google Maps Navigation...')">
              🧭 Directions
            </button>
          </div>
        </div>

        <!-- 5-Step Timeline -->
        <div class="fmw-card">
          <h4 style="font-size: 14px; font-weight: 700; color: var(--text-primary); margin-bottom: 14px;">Job Progression</h4>
          <div class="timeline-box">
            <div class="timeline-step ${currentIdx >= 0 ? 'completed' : ''} ${job.status === 'ACCEPTED' ? 'active' : ''}">
              <div class="timeline-indicator">1</div>
              <div class="timeline-text">
                <h4>Job Accepted</h4>
                <p>Worker assigned & notified customer</p>
              </div>
            </div>

            <div class="timeline-step ${currentIdx >= 1 ? 'completed' : ''} ${job.status === 'ON_THE_WAY' ? 'active' : ''}">
              <div class="timeline-indicator">2</div>
              <div class="timeline-text">
                <h4>On The Way</h4>
                <p>Heading to customer location (ETA 15 mins)</p>
              </div>
            </div>

            <div class="timeline-step ${currentIdx >= 2 ? 'completed' : ''} ${job.status === 'ARRIVED' ? 'active' : ''}">
              <div class="timeline-indicator">3</div>
              <div class="timeline-text">
                <h4>Arrived at Location</h4>
                <p>Customer notified to open the door</p>
              </div>
            </div>

            <div class="timeline-step ${currentIdx >= 3 ? 'completed' : ''} ${job.status === 'STARTED' ? 'active' : ''}">
              <div class="timeline-indicator">4</div>
              <div class="timeline-text">
                <h4>Work in Progress</h4>
                <p>Repair & installation underway</p>
              </div>
            </div>

            <div class="timeline-step ${currentIdx >= 4 ? 'completed' : ''} ${job.status === 'COMPLETED' ? 'active' : ''}">
              <div class="timeline-indicator">5</div>
              <div class="timeline-text">
                <h4>Work Completed</h4>
                <p>Customer verified & payment received</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Interactive Next Action Stage Button -->
        <div style="margin-top: 6px;">
          ${job.status === 'ACCEPTED' ? `
            <button class="btn-app btn-app-primary" style="width: 100%; padding: 14px; font-size: 15px;" onclick="fmwStore.updateActiveJobStatus('ON_THE_WAY'); fmwApp.showToast('🚗 Marked as: On The Way'); fmwApp.renderCurrentScreen();">
              Step 1: Start Traveling ➔
            </button>
          ` : ''}

          ${job.status === 'ON_THE_WAY' ? `
            <button class="btn-app btn-app-primary" style="width: 100%; padding: 14px; font-size: 15px;" onclick="fmwStore.updateActiveJobStatus('ARRIVED'); fmwApp.showToast('📍 Marked as: Arrived at Location'); fmwApp.renderCurrentScreen();">
              Step 2: I Have Arrived ➔
            </button>
          ` : ''}

          ${job.status === 'ARRIVED' ? `
            <button class="btn-app btn-app-amber" style="width: 100%; padding: 14px; font-size: 15px;" onclick="fmwApp.showOtpModal('${job.otp}')">
              Step 3: Enter Customer Start OTP (${job.otp}) ➔
            </button>
          ` : ''}

          ${job.status === 'STARTED' ? `
            <button class="btn-app btn-app-success" style="width: 100%; padding: 14px; font-size: 15px;" onclick="fmwApp.showPaymentCompleteModal('${job.id}', ${job.totalAmount})">
              Step 4: Mark Work Finished & Collect Payment ➔
            </button>
          ` : ''}
        </div>
      </div>
    `;
  },

  // 8. JOB HISTORY SCREEN
  renderJobHistory() {
    const s = fmwStore.get();
    const history = s.jobHistory;

    return `
      <div class="app-top-bar">
        <div>
          <h2 style="font-size: 16px; font-weight: 800; color: var(--text-primary);">Job History</h2>
          <p style="font-size: 11px; color: var(--text-secondary);">${history.length} completed customer orders</p>
        </div>
        <span class="status-chip completed">100% Verified</span>
      </div>

      <div class="screen-content">
        <div class="chips-cloud" style="margin-bottom: 4px;">
          <div class="chip-select selected">All (${history.length})</div>
          <div class="chip-select">5 Star Reviews</div>
          <div class="chip-select">This Month</div>
        </div>

        ${history.map(item => `
          <div class="fmw-card">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 6px;">
              <div>
                <h4 style="font-size: 14px; font-weight: 700; color: var(--text-primary);">${item.subServiceName}</h4>
                <p style="font-size: 11px; color: var(--text-secondary);">👤 ${item.customerName} • ${new Date(item.date).toLocaleDateString('en-IN', { month: 'short', day: 'numeric' })}</p>
              </div>
              <span style="font-size: 15px; font-weight: 800; color: var(--fmw-success);">${window.formatInr(item.totalAmount)}</span>
            </div>

            <div style="display: flex; align-items: center; gap: 4px; font-size: 12px; color: #ffb800; margin: 4px 0;">
              ${'★'.repeat(Math.floor(item.rating))} <span style="color: var(--text-secondary); font-size: 11px; margin-left: 4px;">(${item.rating}.0)</span>
            </div>

            ${item.review ? `
              <p style="font-size: 12px; color: var(--text-secondary); font-style: italic; background: var(--bg-card-subtle); padding: 6px 10px; border-radius: 8px; margin-top: 4px;">
                "${item.review}"
              </p>
            ` : ''}
          </div>
        `).join('')}
      </div>
    `;
  },

  // 9. EARNINGS SCREEN
  renderEarnings() {
    const s = fmwStore.get();
    const earnings = s.earnings;
    const totalEarnings = earnings.reduce((sum, item) => sum + item.amount, 0);
    const pendingPayout = 2450.0;

    return `
      <div class="app-top-bar">
        <div style="display: flex; align-items: center; gap: 8px;">
          <button class="btn-app btn-app-outline" style="padding: 6px 10px; font-size: 12px;" onclick="fmwApp.navigate('home_dashboard')">
            ← Back
          </button>
          <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary);">Earnings & Payouts</h3>
        </div>
        <button class="btn-ctrl btn-amber" style="padding: 4px 10px; font-size: 11px;" onclick="fmwApp.showWithdrawModal(${pendingPayout})">
          Withdraw
        </button>
      </div>

      <div class="screen-content">
        <!-- Big Total Balance Card -->
        <div class="earnings-hero-card">
          <div class="label">Available Payout Balance</div>
          <div class="amount">${window.formatInr(pendingPayout)}</div>
          <div class="hero-footer">
            <span style="font-size: 12px;">Auto-settles daily at 11:59 PM</span>
            <button onclick="fmwApp.showWithdrawModal(${pendingPayout})" style="background: #ffffff; color: #1a3c8f; border: none; padding: 6px 14px; border-radius: 10px; font-size: 12px; font-weight: 700; cursor: pointer;">
              Instant Payout ➔
            </button>
          </div>
        </div>

        <!-- Weekly Earnings Mini Chart Simulator -->
        <div class="fmw-card">
          <div class="section-title" style="margin-bottom: 12px;">
            <span>Weekly Performance</span>
            <span style="font-size: 12px; color: var(--fmw-success); font-weight: 700;">${window.formatInr(8450)} this week</span>
          </div>
          <div style="display: flex; align-items: flex-end; justify-content: space-between; height: 110px; padding-top: 10px; border-bottom: 1px solid var(--border-color); gap: 6px;">
            ${[
              { day: 'Mon', val: 850, h: 45 },
              { day: 'Tue', val: 1200, h: 65 },
              { day: 'Wed', val: 650, h: 35 },
              { day: 'Thu', val: 1800, h: 90 },
              { day: 'Fri', val: 1400, h: 75 },
              { day: 'Sat', val: 2100, h: 100 },
              { day: 'Sun', val: 450, h: 25 }
            ].map(col => `
              <div style="flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px; height: 100%; justify-content: flex-end;">
                <div style="font-size: 9px; color: var(--text-tertiary); font-weight: 600;">₹${col.val}</div>
                <div style="width: 100%; max-width: 24px; height: ${col.h}%; background: linear-gradient(180deg, #60a5fa, #1a3c8f); border-radius: 6px 6px 0 0;"></div>
                <div style="font-size: 11px; color: var(--text-secondary);">${col.day}</div>
              </div>
            `).join('')}
          </div>
        </div>

        <!-- Recent Transactions -->
        <div class="section-title">
          <span>Recent Transactions</span>
        </div>
        <div style="display: flex; flex-direction: column; gap: 8px;">
          ${earnings.map(e => `
            <div class="fmw-card" style="padding: 12px 14px; display: flex; justify-content: space-between; align-items: center;">
              <div>
                <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">${e.customerName}</div>
                <div style="font-size: 11px; color: var(--text-secondary);">${new Date(e.date).toLocaleDateString('en-IN', { month: 'short', day: 'numeric' })} • via ${e.paymentMethod}</div>
              </div>
              <div style="text-align: right;">
                <div style="font-size: 14px; font-weight: 800; color: var(--fmw-success);">${window.formatInr(e.amount)}</div>
                <span class="status-chip ${e.status === 'SETTLED' ? 'completed' : 'pending'}" style="font-size: 9px; padding: 1px 6px;">${e.status}</span>
              </div>
            </div>
          `).join('')}
        </div>
      </div>
    `;
  },

  // 10. PROFILE VIEW SCREEN
  renderProfile() {
    const s = fmwStore.get();
    const w = s.worker;

    return `
      <!-- Hero Banner Header -->
      <div style="background: linear-gradient(180deg, var(--fmw-navy) 0%, #152d6b 100%); padding: 24px 16px 20px 16px; color: #fff; text-align: center; position: relative;">
        <div style="position: absolute; top: 14px; right: 14px; display: flex; gap: 8px;">
          <button onclick="fmwApp.navigate('settings')" style="background: rgba(255,255,255,0.15); border: none; color: #fff; width: 34px; height: 34px; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center;">
            ⚙️
          </button>
        </div>

        <div style="width: 76px; height: 76px; border-radius: 50%; margin: 0 auto 10px auto; border: 3px solid #ffb800; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.3);">
          <img src="${w.profilePhotoUrl}" alt="${w.name}" style="width: 100%; height: 100%; object-fit: cover;" onerror="this.src='https://cdn-icons-png.flaticon.com/512/3135/3135715.png'">
        </div>

        <h2 style="font-size: 18px; font-weight: 800; margin-bottom: 2px;">${w.name}</h2>
        <p style="font-size: 12px; color: #93c5fd; margin-bottom: 8px;">${w.skills.slice(0, 2).join(' • ')} in ${w.city}</p>

        <div style="display: inline-flex; align-items: center; gap: 4px; background: rgba(255, 184, 0, 0.2); border: 1px solid #ffb800; color: #ffb800; padding: 2px 10px; border-radius: 99px; font-size: 11px; font-weight: 700;">
          ★ ${w.rating} Rating (${w.ratingCount} Reviews)
        </div>

        <div style="display: flex; justify-content: center; gap: 8px; margin-top: 14px;">
          <button class="btn-app btn-app-primary" style="background: #ffffff; color: #1a3c8f; padding: 8px 18px; font-size: 12px;" onclick="fmwApp.navigate('profile_edit')">
            ✏️ Edit Profile
          </button>
          <button class="btn-app btn-app-outline" style="border-color: rgba(255,255,255,0.3); color: #fff; padding: 8px 14px; font-size: 12px;" onclick="fmwApp.navigate('payment_methods')">
            💳 Bank & UPI
          </button>
        </div>
      </div>

      <div class="screen-content">
        <!-- Rates Card -->
        <div class="fmw-card">
          <h4 style="font-size: 14px; font-weight: 700; color: var(--text-primary); margin-bottom: 12px;">Standard Rates (₹ INR)</h4>
          <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; text-align: center;">
            <div style="background: var(--bg-card-subtle); padding: 10px; border-radius: 10px;">
              <div style="font-size: 11px; color: var(--text-secondary);">Hourly</div>
              <div style="font-size: 14px; font-weight: 800; color: var(--text-primary); margin-top: 2px;">${window.formatInr(w.hourlyRate)}/hr</div>
            </div>
            <div style="background: var(--bg-card-subtle); padding: 10px; border-radius: 10px;">
              <div style="font-size: 11px; color: var(--text-secondary);">Full Day</div>
              <div style="font-size: 14px; font-weight: 800; color: var(--text-primary); margin-top: 2px;">${window.formatInr(w.dailyRate)}/day</div>
            </div>
            <div style="background: var(--bg-card-subtle); padding: 10px; border-radius: 10px;">
              <div style="font-size: 11px; color: var(--text-secondary);">Radius</div>
              <div style="font-size: 14px; font-weight: 800; color: var(--text-primary); margin-top: 2px;">${w.workingRadiusKm} km</div>
            </div>
          </div>
        </div>

        <!-- Skills & Categories -->
        <div class="fmw-card">
          <h4 style="font-size: 14px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px;">Skills & Specialties</h4>
          <div class="chips-cloud">
            ${w.skills.map(s => `<span class="chip-select selected" style="cursor: default; font-size: 11px; padding: 4px 10px;">${s}</span>`).join('')}
          </div>
        </div>

        <!-- Service Packages -->
        <div class="fmw-card">
          <h4 style="font-size: 14px; font-weight: 700; color: var(--text-primary); margin-bottom: 10px;">Fixed Price Service Packages</h4>
          ${w.packages.map(pkg => `
            <div style="border: 1px solid var(--border-color); border-radius: 10px; padding: 10px; margin-bottom: 8px;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <h5 style="font-size: 13px; font-weight: 700; color: var(--text-primary);">${pkg.title}</h5>
                <span style="font-size: 13px; font-weight: 800; color: var(--fmw-success);">${window.formatInr(pkg.price)}</span>
              </div>
              <p style="font-size: 11px; color: var(--text-secondary); margin-top: 4px;">${pkg.description}</p>
            </div>
          `).join('')}
        </div>

        <!-- KYC Verification Status -->
        <div class="fmw-card" style="display: flex; align-items: center; gap: 12px;">
          <div style="font-size: 24px;">🛡️</div>
          <div>
            <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">Aadhaar & Bank Verified</div>
            <div style="font-size: 11px; color: var(--fmw-success);">Official Find My Work Partner Badge Active</div>
          </div>
        </div>
      </div>
    `;
  },

  // 11. PROFILE EDIT SCREEN
  renderProfileEdit() {
    const s = fmwStore.get();
    const w = s.worker;

    return `
      <div class="app-top-bar">
        <div style="display: flex; align-items: center; gap: 8px;">
          <button class="btn-app btn-app-outline" style="padding: 6px 10px; font-size: 12px;" onclick="fmwApp.navigate('profile')">
            ← Back
          </button>
          <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary);">Edit Worker Profile</h3>
        </div>
        <button class="btn-app btn-app-primary" style="padding: 6px 12px; font-size: 12px;" onclick="fmwScreens.saveProfileEdit()">
          Save
        </button>
      </div>

      <div class="screen-content">
        <div class="fmw-card">
          <div class="form-group">
            <label class="form-label">Full Name</label>
            <input type="text" id="edit_name" class="form-input" value="${w.name}">
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">City</label>
              <input type="text" id="edit_city" class="form-input" value="${w.city}">
            </div>
            <div class="form-group">
              <label class="form-label">Experience (Yrs)</label>
              <input type="number" id="edit_exp" class="form-input" value="${w.experienceYears}">
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Hourly Rate (₹)</label>
              <input type="number" id="edit_hourly" class="form-input" value="${w.hourlyRate}">
            </div>
            <div class="form-group">
              <label class="form-label">Daily Rate (₹)</label>
              <input type="number" id="edit_daily" class="form-input" value="${w.dailyRate}">
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Working Radius (km)</label>
            <input type="number" id="edit_radius" class="form-input" value="${w.workingRadiusKm}">
          </div>

          <div class="form-group">
            <label class="form-label">Bio Description</label>
            <textarea id="edit_bio" class="form-textarea" rows="3">${w.bio}</textarea>
          </div>

          <div class="form-group">
            <label class="form-label">Skills (Comma separated)</label>
            <input type="text" id="edit_skills" class="form-input" value="${w.skills.join(', ')}">
          </div>

          <button class="btn-app btn-app-primary" style="margin-top: 10px; width: 100%;" onclick="fmwScreens.saveProfileEdit()">
            Save Changes ➔
          </button>
        </div>
      </div>
    `;
  },

  saveProfileEdit() {
    const name = document.getElementById('edit_name')?.value;
    const city = document.getElementById('edit_city')?.value;
    const exp = parseInt(document.getElementById('edit_exp')?.value || '5', 10);
    const hourly = parseFloat(document.getElementById('edit_hourly')?.value || '299');
    const daily = parseFloat(document.getElementById('edit_daily')?.value || '1800');
    const radius = parseInt(document.getElementById('edit_radius')?.value || '15', 10);
    const bio = document.getElementById('edit_bio')?.value;
    const skillsStr = document.getElementById('edit_skills')?.value || '';
    const skills = skillsStr.split(',').map(x => x.trim()).filter(Boolean);

    fmwStore.set(st => ({
      ...st,
      worker: {
        ...st.worker,
        name: name || st.worker.name,
        city: city || st.worker.city,
        experienceYears: exp,
        hourlyRate: hourly,
        dailyRate: daily,
        workingRadiusKm: radius,
        bio: bio || st.worker.bio,
        skills
      }
    }));

    fmwApp.showToast('✅ Profile updated successfully in Firestore simulation!');
    fmwApp.navigate('profile');
  },

  // 12. PAYMENT METHODS SCREEN
  renderPaymentMethods() {
    const s = fmwStore.get();
    const w = s.worker;

    return `
      <div class="app-top-bar">
        <div style="display: flex; align-items: center; gap: 8px;">
          <button class="btn-app btn-app-outline" style="padding: 6px 10px; font-size: 12px;" onclick="fmwApp.navigate('settings')">
            ← Back
          </button>
          <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary);">Payout Methods</h3>
        </div>
      </div>

      <div class="screen-content">
        <!-- UPI Section -->
        <div class="fmw-card">
          <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span style="font-size: 20px;">⚡</span>
              <h4 style="font-size: 14px; font-weight: 700; color: var(--text-primary);">UPI ID (Primary)</h4>
            </div>
            <span class="status-chip completed">Active</span>
          </div>
          <p style="font-size: 13px; font-weight: 600; color: var(--text-primary); margin: 4px 0;">${w.upiId}</p>
          <p style="font-size: 11px; color: var(--text-secondary);">Instant daily settlement via PhonePe / Google Pay / BHIM</p>
        </div>

        <!-- Bank Account Section -->
        <div class="fmw-card">
          <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span style="font-size: 20px;">🏦</span>
              <h4 style="font-size: 14px; font-weight: 700; color: var(--text-primary);">Bank Account</h4>
            </div>
            <span class="status-chip completed">Verified</span>
          </div>
          <div style="font-size: 13px; color: var(--text-primary); font-weight: 600;">A/C: •••• •••• 5012</div>
          <div style="font-size: 11px; color: var(--text-secondary); margin-top: 2px;">Holder: ${w.bankHolderName} • IFSC: ${w.bankIfsc}</div>
        </div>

        <button class="btn-app btn-app-primary" onclick="fmwApp.showToast('💳 Bank verification dialog')">
          + Add New Bank Account
        </button>
      </div>
    `;
  },

  // 13. NOTIFICATIONS SCREEN
  renderNotifications() {
    const s = fmwStore.get();
    const notifs = s.notifications;

    return `
      <div class="app-top-bar">
        <div style="display: flex; align-items: center; gap: 8px;">
          <button class="btn-app btn-app-outline" style="padding: 6px 10px; font-size: 12px;" onclick="fmwApp.navigate('home_dashboard')">
            ← Back
          </button>
          <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary);">Notifications</h3>
        </div>
        <button class="btn-ctrl" style="padding: 4px 8px; font-size: 11px;" onclick="fmwStore.set(st => ({ ...st, notifications: st.notifications.map(n => ({...n, read: true})) })); fmwApp.renderCurrentScreen();">
          Mark All Read
        </button>
      </div>

      <div class="screen-content">
        ${notifs.map(n => `
          <div class="fmw-card" style="padding: 12px; display: flex; align-items: flex-start; gap: 10px; border-left: 3px solid ${n.read ? 'transparent' : 'var(--fmw-amber)'}; cursor: pointer;" onclick="fmwApp.navigate('${n.targetScreen || 'home_dashboard'}')">
            <div style="font-size: 20px;">${n.type === 'JOB' ? '⚡' : n.type === 'PAYMENT' ? '💰' : '⭐'}</div>
            <div style="flex: 1;">
              <div style="font-size: 13px; font-weight: 700; color: var(--text-primary);">${n.title}</div>
              <div style="font-size: 12px; color: var(--text-secondary); margin-top: 2px;">${n.message}</div>
              <div style="font-size: 10px; color: var(--text-tertiary); margin-top: 4px;">${new Date(n.date).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
            </div>
          </div>
        `).join('')}
      </div>
    `;
  },

  // 14. SETTINGS SCREEN
  renderSettings() {
    const s = fmwStore.get();
    const isDark = s.theme === 'dark';

    return `
      <div class="app-top-bar">
        <div style="display: flex; align-items: center; gap: 8px;">
          <button class="btn-app btn-app-outline" style="padding: 6px 10px; font-size: 12px;" onclick="fmwApp.navigate('profile')">
            ← Back
          </button>
          <h3 style="font-size: 15px; font-weight: 700; color: var(--text-primary);">Settings</h3>
        </div>
      </div>

      <div class="screen-content">
        <!-- Theme Toggle -->
        <div class="fmw-card" style="display: flex; align-items: center; justify-content: space-between;">
          <div>
            <div style="font-size: 14px; font-weight: 700; color: var(--text-primary);">Dark Theme</div>
            <div style="font-size: 11px; color: var(--text-secondary);">High-contrast dark mode for night work</div>
          </div>
          <label class="toggle-switch">
            <input type="checkbox" id="settings_theme_toggle" ${isDark ? 'checked' : ''} onchange="fmwApp.toggleTheme()">
            <span class="toggle-slider"></span>
          </label>
        </div>

        <!-- Language Selector -->
        <div class="fmw-card">
          <div style="font-size: 14px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px;">App Language</div>
          <select class="form-select" onchange="fmwApp.showToast('Language updated to: ' + this.value)">
            <option value="English" selected>English (English)</option>
            <option value="Hindi">हिन्दी (Hindi)</option>
            <option value="Gujarati">ગુજરાતી (Gujarati)</option>
            <option value="Marathi">मराठी (Marathi)</option>
          </select>
        </div>

        <!-- Support & Helpline -->
        <div class="fmw-card">
          <div style="font-size: 14px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px;">Worker Support</div>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <button class="btn-app btn-app-outline" onclick="fmwApp.showToast('📞 Calling Worker Toll-Free Helpline: 1800-123-9876')">
              📞 24/7 Helpline (1800-123-9876)
            </button>
            <button class="btn-app btn-app-outline" onclick="fmwApp.showToast('💬 Opening WhatsApp Partner Support...')">
              💬 WhatsApp Support Desk
            </button>
          </div>
        </div>

        <!-- App Info -->
        <div style="text-align: center; padding: 12px 0;">
          <div style="font-size: 12px; font-weight: 700; color: var(--text-secondary);">Find My Work Partner Edition</div>
          <div style="font-size: 11px; color: var(--text-tertiary);">Version 1.0.0 (Build 2026.08) • Ready</div>
        </div>

        <!-- Sign Out -->
        <button class="btn-app btn-app-outline" style="color: var(--fmw-danger); border-color: rgba(239,68,68,0.3);" onclick="fmwApp.navigate('login')">
          Sign Out of Account
        </button>
      </div>
    `;
  }
};
