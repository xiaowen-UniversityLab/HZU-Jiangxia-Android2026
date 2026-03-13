package com.example.news.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.news.R;
import com.example.news.database.entity.User;
import com.example.news.databinding.ActivityRegisterBinding;
import com.example.news.repository.UserRepository;

/**
 * 注册页面 Activity
 * 新用户注册账号，输入用户名、密码和确认密码
 */
public class RegisterActivity extends AppCompatActivity {
    
    // 视图绑定对象
    private ActivityRegisterBinding binding;
    // 用户数据仓库
    private UserRepository userRepository;
    
    /**
     * 页面创建时初始化
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用 ViewBinding 初始化视图
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 初始化用户仓库
        userRepository = new UserRepository(this);
        
        // 设置监听器
        setupListeners();
    }
    
    /**
     * 设置各种监听器
     */
    private void setupListeners() {
        // 注册按钮点击事件
        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        
        // 登录文字点击事件（返回登录页）
        binding.tvLogin.setOnClickListener(v -> {
            finish();  // 结束当前 Activity，返回登录页
            // 添加返回动画：从左向右滑入
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
    
    /**
     * 尝试注册
     * 验证输入信息并调用注册接口
     */
    private void attemptRegister() {
        // 获取输入信息并去除首尾空格
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // 验证输入是否为空
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证用户名长度（至少3个字符）
        if (username.length() < 3) {
            binding.tilUsername.setError(getString(R.string.username_too_short));
            return;
        }

        // 验证密码长度（至少6个字符）
        if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.password_too_short));
            return;
        }

        // 验证两次输入的密码是否一致
        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.password_not_match));
            return;
        }

        // 显示加载状态
        showLoading(true);

        // 创建用户对象
        User user = new User(username, password);
        
        // 调用注册接口
        userRepository.register(user, new UserRepository.OnRegisterCallback() {
            @Override
            public void onSuccess() {
                // 注册成功
                showLoading(false);
                Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                finish();  // 返回登录页
            }
            
            @Override
            public void onError(String message) {
                // 注册失败
                showLoading(false);
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     * 显示或隐藏加载状态
     * @param show true 显示加载，false 隐藏加载
     */
    private void showLoading(boolean show) {
        // 显示或隐藏进度条
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        // 禁用或启用注册按钮
        binding.btnRegister.setEnabled(!show);
    }
    
    /**
     * 页面销毁时释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
