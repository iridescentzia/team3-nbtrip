// src/api/openai.js
import axios from 'axios';

export const getAIComment = async (prompt) => {
  try {
    const response = await axios.post(
      'https://api.openai.com/v1/chat/completions',
      {
        model: 'gpt-4o',
        messages: [
          {
            role: 'system',
            content:
              '너는 여행 소비 분석을 도와주는 전문가야. 사용자의 소비 지출 데이터를 비율로 분석하고, 이번 여행에 대해 개선 조언이나 피드백을 전문가처럼 해줘. 너무 딱딱하지 않고, 부드러운 조언을 포함한 한국어로 작성해.',
          },
          { role: 'user', content: prompt },
        ],
        temperature: 0.7,
      },
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${import.meta.env.VITE_OPENAI_API_KEY}`,
        },
      }
    );

    return response.data.choices[0].message.content;
  } catch (error) {
    console.error('OpenAI API 오류:', error);
    throw error;
  }
};
