/** @type {import('tailwindcss').Config} */
const colors = require('tailwindcss/colors')

module.exports = {
 
  darkMode: 'class',
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    colors : {
      red: colors.red,
      blue :colors.blue,
      white : colors.white ,
      green : colors.green,
      purple : colors.purple ,
      yellow : colors.yellow,
      orange : colors.orange ,
      transparent: 'transparent',
      indigo : colors.indigo,
      pink : colors.pink ,
      gray : colors.gray ,
    },
    fontFamily: {
      poppins: ["Poppins"],
    },
    extend: {},
  },
  plugins: [],
}

