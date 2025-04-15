"use strict";
const appNav = document.querySelector('.appNav');
const compNav = document.querySelector('.compNav');
const personNav = document.querySelector('.personNav');
const container = document.querySelector('.content-container');
const hero = document.querySelector('.hero');
const nav = document.querySelector('.navigation');
const hamburger = document.querySelector('.hamburger');
const menucontent = document.querySelector(".menu-content"); //links in navigation

//updates size of iframe
const resizeIframe = () => {
    let heroHeight = hero.clientHeight;
    let navHeight = nav.clientHeight;
    let vpHeight = window.innerHeight;
    container.height = `${vpHeight - heroHeight - navHeight - 30}`;
};

//swithes visibility of menu when clicking
const switchMenuVisibility = () => {
    let vw = document.documentElement.clientWidth;
    if (vw <= 600) { //for max width 600px
        if (menucontent.style.display === 'block') {
            menucontent.style.display = 'none';
        }
        else {
            menucontent.style.display = 'block';
        }
    }
};

appNav.addEventListener("click", () => {
    container.src = "/app.html";
});

compNav.addEventListener("click", () => {
    container.src = "/comp.html";
});

personNav.addEventListener("click", () => {
    container.src = "/person.html";
});

hamburger.addEventListener('click', switchMenuVisibility);
menucontent.addEventListener('click', switchMenuVisibility);
/******************************************************************************************/
window.addEventListener('resize', resizeIframe);

window.addEventListener("resize", () => {
    if (window.innerWidth > 720) {
        menucontent.style.display = 'flex';
    }
    else {
        menucontent.style.display = 'none';
    }
});

window.onload = () => {
    resizeIframe();
};