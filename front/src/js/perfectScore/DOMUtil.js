/**
 * 지정된 속성과 선택적 자식 요소를 사용하여 HTML 요소를 생성합니다.
 * @param {string} tag - HTML 태그 이름입니다.
 * @param {object} attrs - 요소에 설정할 속성을 포함하는 객체입니다.
 * @param {string|Element|Array} children - 선택적 자식 요소 또는 텍스트 내용입니다.
 * @returns {Element} 생성된 HTML 요소입니다.
 */
export function createElem(tag, attrs, children) {
  const el = document.createElement(tag);

  // 속성 설정
  Object.keys(attrs).forEach(key => {
    if (key === "class") {
      let classes = attrs[key];
      if (!Array.isArray(classes)) {
        classes = [classes];
      }
      el.classList.add(...classes); // 클래스 추가
    } else {
      el.setAttribute(key, attrs[key]); // 다른 속성 설정
    }
  });

  // 자식 요소 추가
  if (typeof children === "string") {
    const textNode = document.createTextNode(children);
    el.appendChild(textNode); // 텍스트 내용 추가
  } else if (Array.isArray(children)) {
    children.forEach(child => {
      el.appendChild(child); // 각 자식 요소 추가
    });
  } else if (children instanceof Element) {
    el.appendChild(children); // 단일 자식 요소 추가
  }

  return el;
}
