// WebSocket 연결
let socket;
// 백엔드 URL은 서버에서 주입됨 (__BACKEND_URL__)
const backendUrl = window.BACKEND_URL || 'http://localhost:6060';

// 백엔드 서버 상태 확인
async function checkBackendStatus() {
    try {
        const response = await fetch(`${backendUrl}/api/status`);
        if (response.ok) {
            document.getElementById('backendStatus').textContent = '연결됨';
            document.getElementById('backendStatus').className = 'status-value connected';
        } else {
            throw new Error('Backend not responding');
        }
    } catch (error) {
        document.getElementById('backendStatus').textContent = '연결 안됨';
        document.getElementById('backendStatus').className = 'status-value disconnected';
    }
}

// WebSocket 연결 초기화
function initWebSocket() {
    // Socket.IO 클라이언트 연결 (백엔드에 WebSocket 서버가 있다고 가정)
    // socket = io(backendUrl);
    
    // socket.on('connect', () => {
    //     document.getElementById('wsStatus').textContent = '연결됨';
    //     document.getElementById('wsStatus').className = 'status-value connected';
    // });
    
    // socket.on('disconnect', () => {
    //     document.getElementById('wsStatus').textContent = '연결 안됨';
    //     document.getElementById('wsStatus').className = 'status-value disconnected';
    // });
}

// 이미지 업로드 및 인식
const imageInput = document.getElementById('imageInput');
const imagePreview = document.getElementById('imagePreview');
const imageResult = document.getElementById('imageResult');

imageInput.addEventListener('change', async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // 이미지 미리보기
    const reader = new FileReader();
    reader.onload = (event) => {
        imagePreview.src = event.target.result;
        imagePreview.style.display = 'block';
        imageResult.textContent = '이미지를 분석하는 중...';
    };
    reader.readAsDataURL(file);

    // TensorFlow.js를 사용한 이미지 인식 (예시)
    try {
        // 여기에 이미지 인식 로직 추가
        // 예: MobileNet 모델 로드 및 예측
        imageResult.textContent = '이미지 인식 기능 준비 중... (TensorFlow.js 로드 필요)';
        
        // WebSocket으로 이미지 전송 (선택사항)
        // if (socket && socket.connected) {
        //     socket.emit('image', file);
        // }
    } catch (error) {
        console.error('이미지 인식 오류:', error);
        imageResult.textContent = '이미지 인식 중 오류가 발생했습니다.';
    }
});

// 초기화
checkBackendStatus();
setInterval(checkBackendStatus, 5000); // 5초마다 백엔드 상태 확인
initWebSocket();

