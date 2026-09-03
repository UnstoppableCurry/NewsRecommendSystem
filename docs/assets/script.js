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
