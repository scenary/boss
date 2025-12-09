import express from 'express';
import path from 'path';
import { readFileSync } from 'fs';

const app = express();
const PORT = 5000;
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:6060';

// 정적 파일 제공
app.use(express.static(path.join(process.cwd(), 'public')));

// HTML에 백엔드 URL 주입
app.get('/', (req, res) => {
  const html = readFileSync(path.join(process.cwd(), 'public', 'index.html'), 'utf-8')
    .replace('__BACKEND_URL__', BACKEND_URL);
  res.send(html);
});

app.listen(PORT, () => {
  console.log(`서버 실행: http://localhost:${PORT}`);
  console.log(`백엔드: ${BACKEND_URL}`);
});
