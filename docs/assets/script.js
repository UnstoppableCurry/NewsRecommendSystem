(function () {
  var toggle = document.querySelector("[data-nav-toggle]");
  var nav = document.querySelector("[data-site-nav]");
  var dialog = document.querySelector("[data-lightbox]");
  var dialogImage = dialog ? dialog.querySelector("img") : null;
  var dialogCaption = dialog ? dialog.querySelector("[data-lightbox-caption]") : null;
  var closeButton = dialog ? dialog.querySelector("[data-lightbox-close]") : null;

  if (toggle && nav) {
    toggle.addEventListener("click", function () {
      var open = nav.classList.toggle("is-open");
      toggle.setAttribute("aria-expanded", open ? "true" : "false");
    });
  }

  var reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  document.querySelectorAll('a[href^="#"]').forEach(function (link) {
    link.addEventListener("click", function (event) {
      var id = (link.getAttribute("href") || "").slice(1);
      var target = id ? document.getElementById(id) : null;
      if (!target) {
        return;
      }
      event.preventDefault();
      target.scrollIntoView({
        behavior: reduceMotion ? "auto" : "smooth",
        block: "start"
      });
      if (history.pushState) {
        history.pushState(null, "", "#" + id);
      }
      if (nav) {
        nav.classList.remove("is-open");
      }
      if (toggle) {
        toggle.setAttribute("aria-expanded", "false");
      }
    });
  });

  document.querySelectorAll("[data-shot]").forEach(function (button) {
    button.addEventListener("click", function () {
      if (!dialog || !dialogImage || !dialogCaption) {
        return;
      }
      dialogImage.src = button.getAttribute("data-full") || "";
      dialogImage.alt = button.getAttribute("data-alt") || "";
      dialogCaption.textContent = button.getAttribute("data-caption") || "";
      if (typeof dialog.showModal === "function") {
        dialog.showModal();
        closeButton.focus();
      }
    });
  });

  if (closeButton && dialog) {
    closeButton.addEventListener("click", function () {
      dialog.close();
    });
  }

  if (dialog) {
    dialog.addEventListener("click", function (event) {
      if (event.target === dialog) {
        dialog.close();
      }
    });
  }
})();
