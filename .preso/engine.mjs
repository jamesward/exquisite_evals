// Marp engine with Shiki syntax highlighting (VS Code's TextMate grammars), instead of Marp's built-in highlight.js.
import Shiki from '@shikijs/markdown-it'

const shiki = await Shiki({ theme: 'github-light', langs: ['kotlin', 'python', 'java', 'text', 'bash'] })

export default ({ marp }) => marp.use(shiki)
