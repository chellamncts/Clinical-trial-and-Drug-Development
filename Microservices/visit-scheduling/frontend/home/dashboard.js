// Shared dashboard UI helper (presentation only — no API/business logic).
(function () {
  "use strict";
  document.addEventListener("DOMContentLoaded", function () {
    var sidebar = document.querySelector(".sidebar");
    var burger = document.getElementById("burger");
    if (burger && sidebar) {
      burger.addEventListener("click", function () { sidebar.classList.toggle("open"); });
      sidebar.querySelectorAll(".nav-item").forEach(function (b) {
        b.addEventListener("click", function () {
          if (window.matchMedia("(max-width: 920px)").matches) sidebar.classList.remove("open");
        });
      });
    }

    // Back-to-top button
    var btn = document.createElement("button");
    btn.id = "toTop";
    btn.className = "to-top";
    btn.type = "button";
    btn.setAttribute("aria-label", "Back to top");
    btn.innerHTML = '<i class="bi bi-arrow-up"></i>';
    btn.addEventListener("click", function () { window.scrollTo({ top: 0, behavior: "smooth" }); });
    document.body.appendChild(btn);
    window.addEventListener("scroll", function () {
      btn.classList.toggle("show", window.scrollY > 300);
    }, { passive: true });
  });
})();

