/**
 * 主应用模块 - 民兵管理平台前端
 * SPA 路由 + 页面渲染 + WebSocket
 */

// ==================== 路由配置 ====================
const routes = {
  '/login': { title: '登录', render: renderLoginPage, auth: false },
  '/dashboard': { title: '首页', render: renderDashboard, auth: true },
  '/militia': { title: '民兵信息管理', render: renderMilitia, auth: true },
  '/militia/audit': { title: '人员审批', render: renderAudit, auth: true },
  '/announcement': { title: '通知公告', render: renderAnnouncement, auth: true },
  '/work': { title: '工作管理', render: renderWork, auth: true },
  '/leave': { title: '请销假', render: renderLeave, auth: true },
  '/presence': { title: '人员在场', render: renderPresence, auth: true },
  '/profile': { title: '个人中心', render: renderProfile, auth: true }
};

// ==================== 应用状态 ====================
let currentRoute = '';
let wsClient = null;
let wsConnected = false;

// ==================== 初始化 ====================
function initApp() {
  window.addEventListener('hashchange', handleRoute);
  handleRoute();
}

// ==================== 路由处理 ====================
function handleRoute() {
  const hash = window.location.hash || '#/login';
  const path = hash.replace('#', '');
  const route = routes[path] || routes['/dashboard'];

  // 需要登录但未登录
  if (route.auth && !isLoggedIn()) {
    window.location.hash = '#/login';
    return;
  }

  // 已登录但访问登录页
  if (path === '/login' && isLoggedIn()) {
    window.location.hash = '#/dashboard';
    return;
  }

  currentRoute = path;
  document.title = route.title + ' - 民兵管理平台';

  if (path === '/login') {
    renderLoginPage();
  } else {
    renderAppLayout(route);
  }
}

function isLoggedIn() {
  return !!localStorage.getItem('token');
}

// ==================== 渲染登录页 ====================
function renderLoginPage() {
  const app = document.getElementById('app');
  app.innerHTML = `
    <div class="login-page">
      <div class="login-box">
        <div class="logo">
          <h1>民兵管理平台</h1>
          <p> Militia Management System </p>
        </div>
        <form id="loginForm">
          <div class="form-group">
            <label>账号</label>
            <input type="text" class="form-control" id="account" placeholder="请输入账号" required>
          </div>
          <div class="form-group">
            <label>密码</label>
            <input type="password" class="form-control" id="password" placeholder="请输入密码" required>
          </div>
          <button type="submit" class="btn btn-primary btn-lg" id="loginBtn">登录</button>
        </form>
      </div>
    </div>
  `;

  document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = document.getElementById('loginBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="loading"></span> 登录中...';

    try {
      const res = await api.login(
        document.getElementById('account').value,
        document.getElementById('password').value
      );

      if (res.code === 0 && res.data) {
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('user', JSON.stringify(res.data.user));
        utils.toast('登录成功', 'success');
        window.location.hash = '#/dashboard';
        initWebSocket();
      } else {
        utils.toast(res.msg || '登录失败', 'error');
      }
    } catch (err) {
      utils.toast('网络错误，请稍后重试', 'error');
    } finally {
      btn.disabled = false;
      btn.innerHTML = '登录';
    }
  });
}

// ==================== 渲染应用布局 ====================
function renderAppLayout(route) {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const app = document.getElementById('app');

  app.innerHTML = `
    <div class="app-layout">
      <aside class="sidebar">
        <div class="sidebar-header">
          <h2>民兵管理平台</h2>
          <p>Militia Admin</p>
        </div>
        <nav class="nav-menu">
          ${renderNavItem('/dashboard', 'dashboard', '首页')}
          ${renderNavItem('/presence', 'map', '人员在场')}
          ${renderNavItem('/militia', 'users', '民兵信息')}
          ${renderNavItem('/militia/audit', 'check-circle', '人员审批')}
          ${renderNavItem('/announcement', 'bell', '通知公告')}
          ${renderNavItem('/work', 'briefcase', '工作管理')}
          ${renderNavItem('/leave', 'calendar', '请销假')}
        </nav>
      </aside>
      <div class="main-content">
        <header class="top-header">
          <div class="header-left">
            <div class="breadcrumb">
              <span>首页</span>
              <span>/</span>
              <span>${route.title}</span>
            </div>
          </div>
          <div class="header-right">
            <div class="user-info" onclick="logout()">
              <div class="user-avatar">${(user.name || 'U').charAt(0)}</div>
              <span>${user.name || '用户'}</span>
              <span style="color:var(--text-light);font-size:12px;">(${user.role || '未知角色'})</span>
            </div>
          </div>
        </header>
        <main class="page-container page-fade-in" id="pageContent">
          ${route.render()}
        </main>
      </div>
    </div>
  `;

  // 绑定导航点击
  document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', () => {
      window.location.hash = item.dataset.route;
    });
  });
}

function renderNavItem(path, icon, text) {
  const active = currentRoute === path ? 'active' : '';
  return `<div class="nav-item ${active}" data-route="${path}">
    <span class="icon">${getIcon(icon)}</span>
    <span>${text}</span>
  </div>`;
}

function getIcon(name) {
  const icons = {
    'dashboard': '◈',
    'map': '◎',
    'users': '👤',
    'check-circle': '✓',
    'bell': '🔔',
    'briefcase': '💼',
    'calendar': '📅'
  };
  return icons[name] || '•';
}

function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  if (wsClient) wsClient.disconnect();
  window.location.hash = '#/login';
  utils.toast('已退出登录', 'info');
}

// ==================== 页面组件 ====================

// ----- 仪表盘 -----
function renderDashboard() {
  return `
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue">👤</div>
        <div class="stat-info">
          <h4 id="statTotal">--</h4>
          <p>民兵总人数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">✓</div>
        <div class="stat-info">
          <h4 id="statOnline">--</h4>
          <p>在位人数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">📋</div>
        <div class="stat-info">
          <h4 id="statPending">--</h4>
          <p>待审批</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon red">🔔</div>
        <div class="stat-info">
          <h4 id="statNotice">--</h4>
          <p>未读通知</p>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="card-header"><h3>快捷入口</h3></div>
      <div class="card-body" style="display:flex;gap:12px;flex-wrap:wrap;">
        <button class="btn btn-primary" onclick="window.location.hash='#/militia'">民兵信息管理</button>
        <button class="btn btn-success" onclick="window.location.hash='#/announcement'">发布通知公告</button>
        <button class="btn btn-warning" onclick="window.location.hash='#/militia/audit'">人员审批</button>
        <button class="btn btn-info" onclick="window.location.hash='#/leave'">请销假</button>
      </div>
    </div>
    <div class="card">
      <div class="card-header"><h3>系统公告</h3></div>
      <div class="card-body">
        <div class="empty-state">
          <div class="icon">📢</div>
          <p>暂无系统公告</p>
        </div>
      </div>
    </div>
  `;
}

// ----- 民兵信息管理 -----
function renderMilitia() {
  setTimeout(() => loadMilitiaData(), 0);
  return `
    <div class="page-header">
      <h2>民兵信息管理</h2>
      ${utils.hasPermission('militia:info:add') ? `<button class="btn btn-primary" onclick="showAddMilitiaModal()">+ 添加人员</button>` : ''}
    </div>
    <div class="card">
      <div class="card-body">
        <div class="search-bar">
          <input type="text" class="form-control" id="militiaSearch" placeholder="搜索姓名/手机号..." oninput="filterMilitiaData()">
          <button class="btn btn-default" onclick="loadMilitiaData()">🔍 刷新</button>
        </div>
        <div id="militiaTable"></div>
      </div>
    </div>
  `;
}

async function loadMilitiaData() {
  const container = document.getElementById('militiaTable');
  if (!container) return;
  container.innerHTML = '<div class="empty-state"><div class="loading"></div><p>加载中...</p></div>';

  try {
    const res = await api.getUserList();
    if (res.code === 0 && res.data) {
      // 保存原始数据用于搜索
      window._militiaData = res.data;
      renderMilitiaTable(res.data);
    } else {
      container.innerHTML = '<div class="empty-state"><div class="icon">📭</div><p>暂无数据</p></div>';
    }
  } catch (err) {
    container.innerHTML = '<div class="empty-state"><div class="icon">⚠</div><p>加载失败，请刷新重试</p></div>';
  }
}

function filterMilitiaData() {
  const keyword = document.getElementById('militiaSearch').value.trim().toLowerCase();
  if (!window._militiaData) return;
  
  const filtered = window._militiaData.filter(item => {
    if (!keyword) return true;
    return (item.name && item.name.toLowerCase().includes(keyword)) ||
           (item.phone && item.phone.includes(keyword));
  });
  
  renderMilitiaTable(filtered);
}

function renderMilitiaTable(data) {
  const container = document.getElementById('militiaTable');
  if (!data || data.length === 0) {
    container.innerHTML = '<div class="empty-state"><div class="icon">📭</div><p>暂无数据</p></div>';
    return;
  }

  container.innerHTML = `
    <table class="data-table">
      <thead>
        <tr>
          <th>姓名</th>
          <th>手机号</th>
          <th>性别</th>
          <th>组织</th>
          <th>层级</th>
          <th>角色</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        ${data.map(item => `
          <tr>
            <td>${item.name || '-'}</td>
            <td>${item.phone || '-'}</td>
            <td>${utils.formatGender(item.gender)}</td>
            <td>${item.orgName || '-'}</td>
            <td>${utils.formatOrgLevel(item.orgLevel)}</td>
            <td>${item.role || '-'}</td>
            <td><span class="tag ${item.status === 1 ? 'tag-success' : 'tag-danger'}">${utils.formatUserStatus(item.status)}</span></td>
            <td class="actions">
              <button class="btn btn-sm btn-info">查看</button>
            </td>
          </tr>
        `).join('')}
      </tbody>
    </table>
  `;
}

function showAddMilitiaModal() {
  const mask = document.createElement('div');
  mask.className = 'modal-mask';
  mask.innerHTML = `
    <div class="modal-box large">
      <div class="modal-title">添加民兵人员</div>
      <div class="modal-content">
        <div class="form-row">
          <div class="form-group">
            <label>姓名 *</label>
            <input type="text" class="form-control" id="addName" required>
          </div>
          <div class="form-group">
            <label>手机号 *</label>
            <input type="tel" class="form-control" id="addPhone" required>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>性别</label>
            <select class="form-control" id="addGender">
              <option value="1">男</option>
              <option value="0">女</option>
            </select>
          </div>
          <div class="form-group">
            <label>所属组织ID</label>
            <input type="number" class="form-control" id="addOrgId" placeholder="请输入组织ID">
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>角色ID</label>
            <input type="number" class="form-control" id="addRoleId" placeholder="请输入角色ID">
          </div>
        </div>
      </div>
      <div class="modal-actions">
        <button class="btn btn-default" onclick="this.closest('.modal-mask').remove()">取消</button>
        <button class="btn btn-primary" id="btnConfirmAdd">确定</button>
      </div>
    </div>
  `;
  document.body.appendChild(mask);

  mask.querySelector('#btnConfirmAdd').onclick = async () => {
    const data = {
      name: document.getElementById('addName').value,
      phone: document.getElementById('addPhone').value,
      gender: parseInt(document.getElementById('addGender').value),
      orgId: parseInt(document.getElementById('addOrgId').value) || null,
      roleId: parseInt(document.getElementById('addRoleId').value) || null
    };

    if (!data.name || !data.phone) {
      utils.toast('请填写必填项', 'warning');
      return;
    }

    try {
      const res = await api.addUser(data);
      if (res.code === 0) {
        utils.toast('添加成功', 'success');
        mask.remove();
        loadMilitiaData();
      } else {
        utils.toast(res.msg || '添加失败', 'error');
      }
    } catch (err) {
      utils.toast('请求失败', 'error');
    }
  };
}

// ----- 人员审批 -----
function renderAudit() {
  return `
    <div class="page-header">
      <h2>人员审批</h2>
    </div>
    <div class="card">
      <div class="card-body">
        <div id="auditTable">
          <div class="empty-state">
            <div class="icon">📋</div>
            <p>审批功能需要后端提供申请列表接口</p>
          </div>
        </div>
      </div>
    </div>
  `;
}

// ----- 通知公告 -----
function renderAnnouncement() {
  return `
    <div class="page-header">
      <h2>通知公告</h2>
      ${utils.hasPermission('announcement:create') ? `<button class="btn btn-primary" onclick="showAddAnnounceModal()">+ 发布公告</button>` : ''}
    </div>
    <div class="card">
      <div class="card-header"><h3>公告列表</h3></div>
      <div class="card-body">
        <div id="announceList">
          <div class="empty-state">
            <div class="icon">📢</div>
            <p>暂无公告</p>
          </div>
        </div>
      </div>
    </div>
  `;
}

function showAddAnnounceModal() {
  const mask = document.createElement('div');
  mask.className = 'modal-mask';
  mask.innerHTML = `
    <div class="modal-box large">
      <div class="modal-title">发布通知公告</div>
      <div class="modal-content">
        <div class="form-group">
          <label>标题 *</label>
          <input type="text" class="form-control" id="annTitle" required>
        </div>
        <div class="form-group">
          <label>内容 *</label>
          <textarea class="form-control" id="annContent" required></textarea>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>类型</label>
            <select class="form-control" id="annType">
              <option value="0">通知公告</option>
              <option value="1">教育学习</option>
            </select>
          </div>
          <div class="form-group">
            <label>发布组织ID</label>
            <input type="number" class="form-control" id="annOrgId">
          </div>
        </div>
        <div class="form-group">
          <label>目标组织ID（逗号分隔）</label>
          <input type="text" class="form-control" id="annTargetIds" placeholder="如: 1,2,3">
        </div>
        <div class="form-group">
          <label>状态</label>
          <select class="form-control" id="annStatus">
            <option value="1">直接发布</option>
            <option value="0">存为草稿</option>
          </select>
        </div>
      </div>
      <div class="modal-actions">
        <button class="btn btn-default" onclick="this.closest('.modal-mask').remove()">取消</button>
        <button class="btn btn-primary" id="btnConfirmAnn">发布</button>
      </div>
    </div>
  `;
  document.body.appendChild(mask);

  mask.querySelector('#btnConfirmAnn').onclick = async () => {
    const data = {
      title: document.getElementById('annTitle').value,
      content: document.getElementById('annContent').value,
      type: parseInt(document.getElementById('annType').value),
      publishOrgId: parseInt(document.getElementById('annOrgId').value) || null,
      targetOrgIds: document.getElementById('annTargetIds').value || null,
      status: parseInt(document.getElementById('annStatus').value)
    };

    if (!data.title || !data.content) {
      utils.toast('请填写标题和内容', 'warning');
      return;
    }

    try {
      const res = await api.createAnnouncement(data);
      if (res.code === 0) {
        utils.toast('发布成功', 'success');
        mask.remove();
      } else {
        utils.toast(res.msg || '发布失败', 'error');
      }
    } catch (err) {
      utils.toast('请求失败', 'error');
    }
  };
}

// ----- 工作管理 -----
function renderWork() {
  return `
    <div class="page-header"><h2>工作管理</h2></div>
    <div class="card">
      <div class="card-header"><h3>月工作计划</h3></div>
      <div class="card-body">
        <div class="empty-state">
          <div class="icon">💼</div>
          <p>工作管理模块开发中</p>
          <p style="font-size:12px;margin-top:8px;">包含：月工作计划、总结、专项活动报告</p>
        </div>
      </div>
    </div>
  `;
}

// ----- 请销假 -----
function renderLeave() {
  return `
    <div class="page-header"><h2>请销假</h2></div>
    <div class="card">
      <div class="card-header"><h3>请假申请</h3></div>
      <div class="card-body">
        <div class="empty-state">
          <div class="icon">📅</div>
          <p>请销假模块开发中</p>
          <p style="font-size:12px;margin-top:8px;">包含：请假申请、审批、销假、月度汇总</p>
        </div>
      </div>
    </div>
  `;
}

// ----- 人员在场 -----
function renderPresence() {
  return `
    <div class="page-header"><h2>人员在场管理</h2></div>
    <div class="card">
      <div class="card-header"><h3>管辖区域</h3></div>
      <div class="card-body">
        <div style="background:#e8f4f8;border-radius:8px;padding:40px;text-align:center;">
          <div style="font-size:48px;margin-bottom:16px;">🗺️</div>
          <p style="font-size:16px;color:var(--text);">地图选定工具</p>
          <p style="font-size:13px;color:var(--text-light);margin-top:8px;">
            支持正方形、圆形、多边形勾画方式选定军事机构管辖范围<br>
            默认方圆周边扩散100KM为界限
          </p>
          <div style="margin-top:20px;">
            <button class="btn btn-primary">打开地图工具</button>
          </div>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="card-header"><h3>在位情况统计</h3></div>
      <div class="card-body">
        <div class="empty-state">
          <div class="icon">📊</div>
          <p>在位统计功能开发中</p>
        </div>
      </div>
    </div>
  `;
}

// ----- 个人中心 -----
function renderProfile() {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  return `
    <div class="page-header"><h2>个人中心</h2></div>
    <div class="card">
      <div class="card-header"><h3>基本信息</h3></div>
      <div class="card-body">
        <div class="form-row">
          <div class="form-group">
            <label>姓名</label>
            <input type="text" class="form-control" value="${user.name || ''}" readonly>
          </div>
          <div class="form-group">
            <label>角色</label>
            <input type="text" class="form-control" value="${user.role || ''}" readonly>
          </div>
        </div>
        <div class="form-group">
          <label>权限列表</label>
          <div style="display:flex;flex-wrap:wrap;gap:8px;margin-top:8px;">
            ${(user.permissionList || []).map(p => `<span class="tag tag-info">${p}</span>`).join('')}
          </div>
        </div>
      </div>
    </div>
  `;
}

// ==================== WebSocket ====================
function initWebSocket() {
  if (wsClient) return;

  try {
    const socket = new SockJS('http://localhost:9090/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
      wsConnected = true;
      console.log('WebSocket已连接:', frame);

      // 订阅公告频道
      stompClient.subscribe('/topic/announcements', function(message) {
        const data = JSON.parse(message.body);
        utils.toast(`新公告: ${data.title}`, 'info');
      });

      // 订阅个人通知
      stompClient.subscribe('/user/topic/notifications', function(message) {
        const data = JSON.parse(message.body);
        utils.toast(`新通知: ${data.title}`, 'info');
      });
    }, function(error) {
      console.error('WebSocket连接失败:', error);
      wsConnected = false;
    });

    wsClient = stompClient;
  } catch (err) {
    console.warn('WebSocket初始化失败:', err);
  }
}

// ==================== 启动应用 ====================
document.addEventListener('DOMContentLoaded', initApp);
