/**
 * 이벤트를 발생 및 수신
 */
export class EventEmitter {

  constructor() {
    this._handlers = new Map();
  }
  /**
   * 지정된 이벤트 키에 대한 핸들러를 등록합니다.
   * @param {string} eventKey - 이벤트 키입니다.
   * @param {Function} handler - 이벤트 핸들러 함수입니다.
   * @param {boolean} once - 한 번만 실행할지 여부를 나타내는 플래그입니다. 기본값은 false입니다.
   */
  on(eventKey, handler, once = false) {
    if (!this._handlers.has(eventKey)) {
      this._handlers.set(eventKey, []);
    }

    this._handlers.get(eventKey).push({ handler, once });
  }

  /**
   * 지정된 이벤트를 발생시킵니다.
   * @param {string} eventKey - 발생할 이벤트 키입니다.
   * @param  {...any} args - 이벤트 핸들러에 전달될 인수들입니다.
   */
  emit(eventKey, ...args) {
    if (!this._handlers.has(eventKey)) return;

    const handlers = this._handlers.get(eventKey);

    handlers.forEach(info => {
      info.handler(...args);
    });

    // 한 번만 실행할 핸들러 제거
    for (let i = handlers.length - 1; i >= 0; i--) {
      if (handlers[i].once) {
        handlers.splice(i, 1);
      }
    }
  }
}
