const { createApp, ref } = Vue;

createApp({
  setup() {
    const question = ref('');
    const messages = ref([
      {
        id: 1,
        role: 'ai',
        content: '你好呀！👋 很高兴见到你～我是你的数学小助手老师。今天想学点有趣的数学知识吗？或者你有什么数学问题需要我帮忙解答的吗？😊 （比如：计算题、应用题、图形问题都可以哦）',
        timestamp: '11:00'
      }
    ]);
    const isLoading = ref(false);

    // Get current time in HH:MM format
    const getCurrentTime = () => {
      const now = new Date();
      return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
    };

    // Add a message to the chat
    const addMessage = (role, content) => {
      messages.value.push({
        id: Date.now(),
        role,
        content,
        timestamp: getCurrentTime()
      });
    };

    // Send question to the API
    const sendQuestion = async () => {
      if (!question.value.trim()) {
        alert('请输入一个问题');
        return;
      }

      const userQuestion = question.value;
      
      // Add user question to chat
      addMessage('user', userQuestion);
      
      // Clear input
      question.value = '';
      
      // Show loading state
      isLoading.value = true;
      
      try {
        // TODO: Replace this with actual API call when backend is ready
        // Uncomment the following code and remove the simulation when ready:
        /*
        const response = await fetch('/api/question/ask', {
          method: 'POST',
          headers: {
            'Content-Type': 'text/plain'
          },
          body: userQuestion
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Handle the response as a JSON array
        const responseData = await response.json();
        */
        
        // SIMULATION CODE - Remove when connecting to real API
        // Simulate API delay
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Simulate API response based on question
        const responses = {
          '你好': '你好！很高兴见到你！有什么我可以帮助你的吗？',
          '加法': '加法是数学中最基本的运算之一。例如：2 + 3 = 5。你想练习一些加法题目吗？',
          '减法': '减法是加法的逆运算。例如：5 - 3 = 2。你需要我出一些减法题给你练习吗？',
          '乘法': '乘法可以看作是重复的加法。例如：3 × 4 = 12，表示3个4相加。你想学习乘法口诀表吗？',
          '除法': '除法是乘法的逆运算。例如：12 ÷ 3 = 4。需要注意的是，0不能作为除数哦！',
          '默认': '这是一个很好的问题！让我来详细解释一下。在数学中，我们有很多有趣的规律和方法可以解决这类问题。首先，我们需要理解题目的意思，然后选择合适的方法来解决它。'
        };
        
        // Find appropriate response or use default
        const lowerQuestion = userQuestion.toLowerCase();
        let responseText = responses['默认'];
        for (const [key, value] of Object.entries(responses)) {
          if (lowerQuestion.includes(key)) {
            responseText = value;
            break;
          }
        }
        
        // Simulate the actual API response format
        const responseData = [{
          result: {
            output: {
              text: responseText
            }
          }
        }];
        // END SIMULATION CODE
        
        let fullResponse = '';
        
        // Process each item in the response array
        for (const item of responseData) {
          if (item.results && item.results.length > 0) {
            const text = item.results[0].output.text || '';
            fullResponse += text;
          } else if (item.result && item.result.output) {
            const text = item.result.output.text || '';
            fullResponse += text;
          }
        }
        
        // Add AI response to chat
        if (fullResponse) {
          addMessage('ai', fullResponse);
        } else {
          addMessage('ai', '没有收到回复内容。');
        }
      } catch (error) {
        console.error('Error:', error);
        addMessage('ai', '发生错误: ' + error.message);
      } finally {
        isLoading.value = false;
      }
    };

    // Handle Enter key press
    const handleKeyPress = (event) => {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendQuestion();
      }
    };

    return {
      question,
      messages,
      isLoading,
      sendQuestion,
      handleKeyPress
    };
  }
}).mount('#app');