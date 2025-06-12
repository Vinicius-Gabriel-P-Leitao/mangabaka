import { createRouter, createWebHistory } from "vue-router";
import Home from "../view/Home.vue";
import About from "../view/About.vue";
import NotFound from "../view/NotFound.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/about",
    name: "About",
    component: About,
  },

  // NOTE: Necessário estar em ultimo
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: NotFound,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
