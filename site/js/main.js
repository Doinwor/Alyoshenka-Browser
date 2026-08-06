/* Лёгкие взаимодействия лендинга Алешеньки */

(function () {
  // Флаг: JS включён (иначе карточки не прячем — страница видна всегда)
  document.documentElement.classList.add('js');

  var revealItems = document.querySelectorAll('.card, .feature, .why-item, .step, .faq-list details');

  if ('IntersectionObserver' in window) {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed');
          io.unobserve(entry.target);
        }
      });
    }, { threshold: 0.12 });
    revealItems.forEach(function (el) { io.observe(el); });
  } else {
    // Без поддержки IntersectionObserver показываем всё сразу
    revealItems.forEach(function (el) { el.classList.add('revealed'); });
  }
})();