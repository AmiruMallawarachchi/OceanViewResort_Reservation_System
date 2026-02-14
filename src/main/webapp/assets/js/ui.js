// Basic UI helpers shared across pages

(function () {
  function initPasswordToggle(toggleId, inputId) {
    var toggle = document.getElementById(toggleId);
    var input = document.getElementById(inputId);
    if (!toggle || !input) return;
    toggle.addEventListener("click", function () {
      var isPassword = input.type === "password";
      input.type = isPassword ? "text" : "password";
      toggle.setAttribute("aria-label", isPassword ? "Hide password" : "Show password");
      toggle.setAttribute("title", isPassword ? "Hide password" : "Show password");
      toggle.classList.toggle("password-toggle--visible", !isPassword);
    });
  }

  function initPasswordStrength(options) {
    if (!options || !options.inputId) return;
    var input = document.getElementById(options.inputId);
    var wrap = document.getElementById(options.wrapId);
    var text = document.getElementById(options.textId);
    var fill = document.getElementById(options.fillId);
    var list = document.getElementById(options.listId);
    if (!input || !wrap || !text || !fill || !list) return;

    function update() {
      var value = input.value || "";
      var checks = {
        length: value.length >= 8,
        lower: /[a-z]/.test(value),
        upper: /[A-Z]/.test(value),
        number: /[0-9]/.test(value),
        special: /[^A-Za-z0-9]/.test(value),
      };
      var passed = 0;
      Object.keys(checks).forEach(function (key) {
        if (checks[key]) passed++;
        var item = list.querySelector('[data-rule="' + key + '"]');
        if (item) item.classList.toggle("ok", checks[key]);
      });
      var strength = "Weak";
      if (passed >= 5) strength = "Strong";
      else if (passed >= 3) strength = "Good";
      wrap.setAttribute("data-strength", strength.toLowerCase());
      text.textContent = "Strength: " + strength;
      fill.style.width = Math.round((passed / 5) * 100) + "%";
    }

    input.addEventListener("input", update);
    update();
  }

  function initCurrentDateTime() {
    var targets = document.querySelectorAll("[data-current-datetime]");
    if (!targets.length) return;
    function format(dt) {
      return dt.toLocaleString(undefined, {
        year: "numeric",
        month: "short",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    }
    function tick() {
      var now = new Date();
      var value = format(now);
      targets.forEach(function (el) {
        el.textContent = value;
      });
    }
    tick();
    setInterval(tick, 60000);
  }

  function initLoginPasswordStrength() {
    var input = document.getElementById("loginPassword");
    var wrap = document.getElementById("loginPasswordStrength");
    var text = document.getElementById("loginPasswordStrengthText");
    var fill = document.getElementById("loginPasswordStrengthFill");
    if (!input || !wrap || !text || !fill) return;
    function update() {
      var value = input.value || "";
      var checks = {
        length: value.length >= 8,
        lower: /[a-z]/.test(value),
        upper: /[A-Z]/.test(value),
        number: /[0-9]/.test(value),
        special: /[^A-Za-z0-9]/.test(value),
      };
      var passed = 0;
      Object.keys(checks).forEach(function (key) {
        if (checks[key]) passed++;
      });
      var strength = "Weak";
      if (passed >= 5) strength = "Strong";
      else if (passed >= 3) strength = "Good";
      else if (value.length > 0) strength = "Weak";
      else strength = "—";
      wrap.setAttribute("data-strength", strength === "—" ? "weak" : strength.toLowerCase());
      text.textContent = strength === "—" ? "Strength: —" : "Strength: " + strength;
      fill.style.width = value.length === 0 ? "0%" : Math.round((passed / 5) * 100) + "%";
    }
    input.addEventListener("input", update);
    input.addEventListener("change", update);
    update();
  }

  document.addEventListener("DOMContentLoaded", function () {
    initCurrentDateTime();

    // Login page
    initPasswordToggle("toggleLoginPassword", "loginPassword");
    initLoginPasswordStrength();

    // User create password (admin users page)
    initPasswordToggle("toggleCreatePassword", "createPassword");
  });

  // Expose helpers for pages that want custom strength meters
  window.OceanViewUI = {
    initPasswordStrength: initPasswordStrength,
    initPasswordToggle: initPasswordToggle,
  };
})();

