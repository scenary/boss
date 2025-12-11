import { apiClient } from './api';

export const guestLogin = async (nickname: string): Promise<any> => {
  const response = await apiClient.post('/api/auth/guest', { nickname });
  return response.data;
};

export const discordLogin = (): void => {
  // 개발 환경에서는 프록시 사용, 프로덕션에서는 직접 URL 사용
  const discordAuthUrl = import.meta.env.DEV 
    ? '/api/auth/discord'  // 개발: 프록시 사용
    : `${import.meta.env.VITE_BACKEND_URL || (window as any).BACKEND_URL || 'http://localhost:8080'}/api/auth/discord`;  // 프로덕션: 직접 URL
  
  window.location.href = discordAuthUrl;
};

// 서버 keep-alive를 위한 health check
export const healthCheck = async (): Promise<boolean> => {
  try {
    await apiClient.get('/api/auth/health');
    return true;
  } catch (error) {
    // 에러가 발생해도 조용히 처리 (서버가 sleep 상태일 수 있으므로)
    console.debug('Health check failed:', error);
    return false;
  }
};