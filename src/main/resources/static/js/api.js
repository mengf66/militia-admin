/**
 * API 封装模块
 * 后端地址: http://localhost:9090
 */
const API_BASE = 'http://localhost:9090';

const api = {
  /**
   * 通用请求封装
   */
  async request(url, options = {}) {
    const token = localStorage.getItem('token');
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };
    if (token) {
      headers['Authorization'] = 'Bearer ' + token;
    }

    try {
      const response = await fetch(API_BASE + url, {
        ...options,
        headers
      });
      
      if (response.status === 401 || response.status === 403) {
        // 未授权，清除登录状态并跳转登录页
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.hash = '#/login';
        throw new Error('登录已过期，请重新登录');
      }

      const data = await response.json();
      return data;
    } catch (err) {
      console.error('API请求失败:', url, err);
      throw err;
    }
  },

  get(url) {
    return this.request(url, { method: 'GET' });
  },

  post(url, body) {
    return this.request(url, {
      method: 'POST',
      body: JSON.stringify(body)
    });
  },

  // ==================== 认证相关 ====================
  login(account, password) {
    return this.post('/auth/login', { account, password });
  },

  // ==================== 用户/民兵管理 ====================
  getUserList() {
    return this.get('/user/show');
  },

  addUser(userData) {
    return this.post('/user/add', userData);
  },

  approveUser(approveData) {
    return this.post('/user/approve', approveData);
  },

  // ==================== 公告管理 ====================
  // 注意: 后端 AnnouncementController.create 方法中判断条件写反了
  // if(announcement != null) 应为 if(announcement == null)
  // 需要修复后端才能正常使用
  createAnnouncement(data) {
    return this.post('/announcement/add', data);
  }
};
