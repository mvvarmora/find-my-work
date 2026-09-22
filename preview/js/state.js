/**
 * Find My Work — State Management & Reactive Data Store
 * Simulates Firestore repository, listeners, and worker lifecycle.
 */

const FMW_STORAGE_KEY = 'fmw_worker_preview_data_v1';

// Initial Canonical Seed Data
const DEFAULT_INITIAL_STATE = {
  theme: 'dark', // 'dark' | 'light'
  currentScreen: 'splash',
  isLoggedIn: true,
  hasCompletedProfile: true,
  worker: {
    id: 'worker_rajesh_01',
    name: 'Rajesh Varmora',
    phone: '+91 98765 43210',
    email: 'rajesh.varmora@example.com',
    age: 32,
    gender: 'Male',
    city: 'Ahmedabad',
    experienceYears: 7,
    bio: 'Certified Master Electrician & Home Appliance Specialist. 7+ years delivering safe wiring, invertor repairs, and fast 24/7 service in Ahmedabad.',
    profilePhotoUrl: 'https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=400&auto=format&fit=crop&q=80',
    skills: ['Fan Installation', 'Inverter Setup', 'Short Circuit Fix', 'MCB Wiring', 'AC Installation', 'Switchboard Repair'],
    categoryIds: ['electrician', 'appliance'],
    workingRadiusKm: 15,
    hourlyRate: 299.0,
    dailyRate: 1800.0,
    monthlyRate: 32000.0,
    perService: [
      { serviceName: 'Ceiling Fan Installation / Repair', price: 249.0 },
      { serviceName: 'Main Switchboard & MCB Troubleshooting', price: 499.0 },
      { serviceName: 'Complete Room Wiring Overhaul', price: 1299.0 }
    ],
    upiId: 'rajesh.varmora@oksbi',
    bankHolderName: 'Rajesh Varmora',
    bankAccountNumber: '918273645012',
    bankIfsc: 'SBIN0001824',
    availableAfterHours: true,
    packages: [
      {
        id: 'pkg_1',
        title: 'Full Home Electrical Health Check',
        price: 1499.0,
        description: 'Covers all switches, earth testing, MCB safety inspection, and wiring audit with report.'
      },
      {
        id: 'pkg_2',
        title: 'Inverter & Battery Setup Package',
        price: 899.0,
        description: 'Complete battery water top-up, wiring terminal cleaning, and inverter load balancing.'
      }
    ],
    offers: [
      {
        id: 'off_1',
        title: 'First-Time Customer 15% OFF',
        discountPercent: 15,
        code: 'WELCOME15'
      }
    ],
    documentsVerified: true,
    isOnline: true,
    rating: 4.88,
    ratingCount: 124,
    ratingSum: 605.12,
    completionRate: 98.4,
    totalJobs: 142,
    totalEarnings: 184500.0
  },
  availableJobs: [
    {
      id: 'job_101',
      customerName: 'Priya Sharma',
      customerPhone: '+91 98250 11223',
      subServiceName: 'Ceiling Fan Sparking & Speed Issue',
      category: 'Electrician',
      specialInstructions: 'Fan is making buzzing sound and sparks at regulator. Please bring high quality capacitor and spare switch.',
      flatNo: 'B-402',
      societyName: 'Shivalik Heights',
      landmark: 'Near ISKCON Cross Road',
      city: 'Ahmedabad',
      totalAmount: 450.0,
      distanceKm: 2.1,
      postedTimeAgo: '4 mins ago',
      isEmergency: true,
      status: 'PENDING',
      createdAt: Date.now() - 4 * 60 * 1000
    },
    {
      id: 'job_102',
      customerName: 'Amit Patel',
      customerPhone: '+91 97241 88990',
      subServiceName: 'Main MCB Tripping Continuously',
      category: 'Electrician',
      specialInstructions: 'Power goes off as soon as AC turns on. Urgent fix needed before evening.',
      flatNo: 'A-12',
      societyName: 'Galaxy Residency',
      landmark: 'Behind Rajpath Club, SG Highway',
      city: 'Ahmedabad',
      totalAmount: 850.0,
      distanceKm: 3.8,
      postedTimeAgo: '12 mins ago',
      isEmergency: true,
      status: 'PENDING',
      createdAt: Date.now() - 12 * 60 * 1000
    },
    {
      id: 'job_103',
      customerName: 'Meera Trivedi',
      customerPhone: '+91 99090 33445',
      subServiceName: 'New Inverter Setup & Wiring',
      category: 'Electrician',
      specialInstructions: 'Purchased Microtek 1200VA inverter. Need dual battery connection setup.',
      flatNo: 'House No 18',
      societyName: 'Sopan Society',
      landmark: 'Vastrapur Lake Road',
      city: 'Ahmedabad',
      totalAmount: 1200.0,
      distanceKm: 5.4,
      postedTimeAgo: '28 mins ago',
      isEmergency: false,
      status: 'PENDING',
      createdAt: Date.now() - 28 * 60 * 1000
    }
  ],
  activeJob: {
    id: 'job_act_01',
    customerName: 'Hardik Shah',
    customerPhone: '+91 98980 55667',
    subServiceName: 'Power Socket Installation & Kitchen Wiring',
    category: 'Electrician',
    specialInstructions: 'Need 16A power socket for Microwave & Chimney. Drill work involved.',
    flatNo: 'Flat 301, Tower C',
    societyName: 'Godrej Garden City',
    landmark: 'Behind Nirma University, SG Highway',
    city: 'Ahmedabad',
    totalAmount: 750.0,
    status: 'ACCEPTED', // 'ACCEPTED' | 'ON_THE_WAY' | 'ARRIVED' | 'STARTED' | 'COMPLETED'
    otp: '4829',
    acceptedAt: Date.now() - 15 * 60 * 1000,
    onTheWayAt: null,
    arrivedAt: null,
    startedAt: null,
    completedAt: null
  },
  jobHistory: [
    {
      id: 'job_hist_01',
      customerName: 'Bhavin Mehta',
      subServiceName: 'Emergency Fuse Wire & MCB Replacement',
      totalAmount: 650.0,
      date: Date.now() - 24 * 3600 * 1000,
      status: 'COMPLETED',
      rating: 5,
      review: 'Super fast arrival within 15 mins! Clean work and very polite.'
    },
    {
      id: 'job_hist_02',
      customerName: 'Sneha Shah',
      subServiceName: '3x LED Downlight & Chandelier Fitment',
      totalAmount: 1100.0,
      date: Date.now() - 2 * 24 * 3600 * 1000,
      status: 'COMPLETED',
      rating: 5,
      review: 'Very professional, did not make any mess and checked all connections.'
    },
    {
      id: 'job_hist_03',
      customerName: 'Karan Desai',
      subServiceName: 'Split AC Power Board Fixing',
      totalAmount: 400.0,
      date: Date.now() - 3 * 24 * 3600 * 1000,
      status: 'COMPLETED',
      rating: 4.5,
      review: 'Good work on short notice.'
    },
    {
      id: 'job_hist_04',
      customerName: 'Nilesh Rao',
      subServiceName: 'Geyser Wiring Short Circuit',
      totalAmount: 550.0,
      date: Date.now() - 5 * 24 * 3600 * 1000,
      status: 'COMPLETED',
      rating: 5,
      review: 'Solved complicated grounding problem very efficiently.'
    }
  ],
  earnings: [
    { id: 'tx_01', customerName: 'Hardik Shah', amount: 750.0, date: Date.now() - 1 * 3600 * 1000, status: 'PROCESSING', paymentMethod: 'UPI' },
    { id: 'tx_02', customerName: 'Bhavin Mehta', amount: 650.0, date: Date.now() - 24 * 3600 * 1000, status: 'SETTLED', paymentMethod: 'UPI' },
    { id: 'tx_03', customerName: 'Sneha Shah', amount: 1100.0, date: Date.now() - 2 * 24 * 3600 * 1000, status: 'SETTLED', paymentMethod: 'Cash' },
    { id: 'tx_04', customerName: 'Karan Desai', amount: 400.0, date: Date.now() - 3 * 24 * 3600 * 1000, status: 'SETTLED', paymentMethod: 'Bank Transfer' },
    { id: 'tx_05', customerName: 'Nilesh Rao', amount: 550.0, date: Date.now() - 5 * 24 * 3600 * 1000, status: 'SETTLED', paymentMethod: 'UPI' }
  ],
  notifications: [
    {
      id: 'notif_01',
      title: '⚡ New Urgent Job Alert',
      message: 'Ceiling Fan Sparking & Speed Issue nearby (2.1 km) - ₹450',
      type: 'JOB',
      read: false,
      date: Date.now() - 5 * 60 * 1000,
      targetScreen: 'available_jobs'
    },
    {
      id: 'notif_02',
      title: '💰 Daily Payout Credited',
      message: '₹2,150 has been settled to your UPI (rajesh.varmora@oksbi)',
      type: 'PAYMENT',
      read: false,
      date: Date.now() - 8 * 3600 * 1000,
      targetScreen: 'earnings'
    },
    {
      id: 'notif_03',
      title: '⭐ 5-Star Review Received',
      message: 'Bhavin Mehta gave you 5 stars: "Super fast arrival and clean work!"',
      type: 'REVIEW',
      read: true,
      date: Date.now() - 24 * 3600 * 1000,
      targetScreen: 'job_history'
    }
  ],
  categoriesList: [
    { id: 'electrician', name: 'Electrician', icon: '⚡' },
    { id: 'plumber', name: 'Plumber', icon: '🔧' },
    { id: 'carpenter', name: 'Carpenter', icon: '🪚' },
    { id: 'ac_repair', name: 'AC & Appliance', icon: '❄️' },
    { id: 'painter', name: 'Painter', icon: '🎨' },
    { id: 'cleaning', name: 'Home Deep Cleaning', icon: '✨' },
    { id: 'pest_control', name: 'Pest Control', icon: '🐜' },
    { id: 'mechanic', name: 'Two-Wheeler Mechanic', icon: '🛵' }
  ]
};

class StateStore {
  constructor() {
    this.listeners = [];
    this.state = this.loadState();
  }

  loadState() {
    try {
      const saved = localStorage.getItem(FMW_STORAGE_KEY);
      if (saved) {
        return JSON.parse(saved);
      }
    } catch (e) {
      console.warn('LocalStorage error:', e);
    }
    return JSON.parse(JSON.stringify(DEFAULT_INITIAL_STATE));
  }

  saveState() {
    try {
      localStorage.setItem(FMW_STORAGE_KEY, JSON.stringify(this.state));
    } catch (e) {
      console.warn('LocalStorage save error:', e);
    }
  }

  get() {
    return this.state;
  }

  set(updater) {
    if (typeof updater === 'function') {
      this.state = updater(this.state);
    } else {
      this.state = { ...this.state, ...updater };
    }
    this.saveState();
    this.notify();
  }

  subscribe(listener) {
    this.listeners.push(listener);
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener);
    };
  }

  notify() {
    this.listeners.forEach(fn => fn(this.state));
  }

  reset() {
    this.state = JSON.parse(JSON.stringify(DEFAULT_INITIAL_STATE));
    this.saveState();
    this.notify();
  }

  // Business Action Helpers
  toggleOnline() {
    const newState = !this.state.worker.isOnline;
    this.set(s => ({
      ...s,
      worker: { ...s.worker, isOnline: newState }
    }));
    return newState;
  }

  acceptJob(jobId) {
    const job = this.state.availableJobs.find(j => j.id === jobId);
    if (!job) return null;

    const newActiveJob = {
      ...job,
      status: 'ACCEPTED',
      otp: Math.floor(1000 + Math.random() * 9000).toString(),
      acceptedAt: Date.now()
    };

    this.set(s => ({
      ...s,
      availableJobs: s.availableJobs.filter(j => j.id !== jobId),
      activeJob: newActiveJob,
      notifications: [
        {
          id: 'notif_' + Date.now(),
          title: '🎉 Job Accepted!',
          message: `You accepted ${newActiveJob.subServiceName} for ${newActiveJob.customerName}`,
          type: 'JOB',
          read: false,
          date: Date.now(),
          targetScreen: 'active_job'
        },
        ...s.notifications
      ]
    }));

    return newActiveJob;
  }

  updateActiveJobStatus(nextStatus) {
    if (!this.state.activeJob) return;

    if (nextStatus === 'COMPLETED') {
      const active = this.state.activeJob;
      const amount = active.totalAmount;
      const historyItem = {
        id: 'job_hist_' + Date.now(),
        customerName: active.customerName,
        subServiceName: active.subServiceName,
        totalAmount: amount,
        date: Date.now(),
        status: 'COMPLETED',
        rating: 5,
        review: 'Work completed smoothly on time!'
      };

      const earningItem = {
        id: 'tx_' + Date.now(),
        customerName: active.customerName,
        amount: amount,
        date: Date.now(),
        status: 'SETTLED',
        paymentMethod: 'UPI'
      };

      this.set(s => ({
        ...s,
        activeJob: null,
        jobHistory: [historyItem, ...s.jobHistory],
        earnings: [earningItem, ...s.earnings],
        worker: {
          ...s.worker,
          totalJobs: s.worker.totalJobs + 1,
          totalEarnings: s.worker.totalEarnings + amount
        }
      }));
    } else {
      this.set(s => ({
        ...s,
        activeJob: {
          ...s.activeJob,
          status: nextStatus,
          [nextStatus.toLowerCase() + 'At']: Date.now()
        }
      }));
    }
  }

  simulateIncomingJob() {
    const categories = ['Electrician', 'Plumber', 'AC Repair', 'Carpenter'];
    const names = ['Vikram Rathore', 'Ananya Deshmukh', 'Rajeev Mehra', 'Kavita Joshi', 'Rohan Gupta'];
    const services = [
      'Split AC Deep Filter & Gas Leak Check',
      'Under-sink Water Leakage Repair',
      'Smart TV Wall Mount Installation',
      'Door Lock & Latch Replacement',
      'Emergency Water Motor Wiring'
    ];
    const streets = ['Bodakdev', 'Prahlad Nagar', 'Navrangpura', 'Satellite', 'Drive-in Road'];

    const chosenName = names[Math.floor(Math.random() * names.length)];
    const chosenService = services[Math.floor(Math.random() * services.length)];
    const chosenStreet = streets[Math.floor(Math.random() * streets.length)];
    const amount = Math.floor(Math.random() * 8 + 3) * 100 + 50;

    const newJob = {
      id: 'job_' + Date.now(),
      customerName: chosenName,
      customerPhone: '+91 ' + Math.floor(9000000000 + Math.random() * 999999999),
      subServiceName: chosenService,
      category: 'General',
      specialInstructions: 'Customer requested immediate technician arrival. Tools required.',
      flatNo: 'Flat ' + Math.floor(Math.random() * 900 + 100),
      societyName: chosenStreet + ' Greens',
      landmark: 'Near City Mall',
      city: 'Ahmedabad',
      totalAmount: amount,
      distanceKm: (Math.random() * 3 + 0.8).toFixed(1),
      postedTimeAgo: 'Just now',
      isEmergency: Math.random() > 0.4,
      status: 'PENDING',
      createdAt: Date.now()
    };

    this.set(s => ({
      ...s,
      availableJobs: [newJob, ...s.availableJobs],
      notifications: [
        {
          id: 'notif_' + Date.now(),
          title: '🚨 New Nearby Job Request!',
          message: `${chosenService} from ${chosenName} (₹${amount})`,
          type: 'JOB',
          read: false,
          date: Date.now(),
          targetScreen: 'available_jobs'
        },
        ...s.notifications
      ]
    }));

    return newJob;
  }
}

// Global store instance
window.fmwStore = new StateStore();

// Utility Currency Formatter (Exact INR match formatInr)
window.formatInr = function(amount) {
  if (amount == null) return '₹0';
  return '₹' + Number(amount).toLocaleString('en-IN', {
    maximumFractionDigits: 0
  });
};
