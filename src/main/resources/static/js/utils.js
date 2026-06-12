/**
 * 工具函数模块
 */
const utils = {
  /**
   * 格式化日期
   */
  formatDate(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  },

  /**
   * 性别转换
   */
  formatGender(gender) {
    const map = { 0: '女', 1: '男', 2: '保密' };
    return map[gender] || '未知';
  },

  /**
   * 组织层级转换
   */
  formatOrgLevel(level) {
    const map = {
      0: '民兵',
      1: '营部/连/分队',
      2: '团机关',
      3: '师机关',
      4: '军机关'
    };
    return map[level] || '未知';
  },

  /**
   * 组织类型转换
   */
  formatOrgType(type) {
    const map = {
      0: '民兵',
      1: '营部',
      2: '连',
      3: '分队',
      4: '团机关',
      5: '师机关',
      6: '军机关'
    };
    return map[type] || '未知';
  },

  /**
   * 审批状态转换
   */
  formatAuditStatus(status) {
    const map = { 0: '已通过', 1: '已驳回', 2: '待审批' };
    return map[status] || '未知';
  },

  /**
   * 账号状态转换
   */
  formatUserStatus(status) {
    const map = { 0: '冻结', 1: '正常' };
    return map[status] || '未知';
  },

  /**
   * 公告类型转换
   */
  formatAnnounceType(type) {
    const map = { 0: '通知公告', 1: '教育学习' };
    return map[type] || '未知';
  },

  /**
   * 公告状态转换
   */
  formatAnnounceStatus(status) {
    const map = { 0: '草稿', 1: '已发布', 2: '已撤回' };
    return map[status] || '未知';
  },

  /**
   * 检查是否有权限
   */
  hasPermission(permission) {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (!user.permissionList) return false;
    return user.permissionList.includes(permission);
  },

  /**
   * 检查是否有角色
   */
  hasRole(role) {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    return user.role === role;
  },

  /**
   * 防抖函数
   */
  debounce(fn, delay = 300) {
    let timer = null;
    return function (...args) {
      clearTimeout(timer);
      timer = setTimeout(() => fn.apply(this, args), delay);
    };
  },

  /**
   * 显示消息提示
   */
  toast(message, type = 'info') {
    const toastEl = document.createElement('div');
    toastEl.className = `toast toast-${type}`;
    toastEl.textContent = message;
    document.body.appendChild(toastEl);
    
    // 触发动画
    requestAnimationFrame(() => {
      toastEl.classList.add('show');
    });

    setTimeout(() => {
      toastEl.classList.remove('show');
      setTimeout(() => toastEl.remove(), 300);
    }, 3000);
  },

  /**
   * 确认对话框
   */
  confirm(message) {
    return new Promise((resolve) => {
      const mask = document.createElement('div');
      mask.className = 'modal-mask';
      mask.innerHTML = `
        <div class="modal-box">
          <div class="modal-title">确认</div>
          <div class="modal-content">${message}</div>
          <div class="modal-actions">
            <button class="btn btn-default" id="btn-cancel">取消</button>
            <button class="btn btn-primary" id="btn-ok">确定</button>
          </div>
        </div>
      `;
      document.body.appendChild(mask);

      mask.querySelector('#btn-ok').onclick = () => {
        mask.remove();
        resolve(true);
      };
      mask.querySelector('#btn-cancel').onclick = () => {
        mask.remove();
        resolve(false);
      };
    });
  }
};
