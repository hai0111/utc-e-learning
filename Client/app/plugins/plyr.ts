import "vue-plyr/dist/vue-plyr.css";
// @ts-ignore
import VuePlyr from "vue-plyr";

export default defineNuxtPlugin((app) => {
  app.vueApp.use(VuePlyr);
});
