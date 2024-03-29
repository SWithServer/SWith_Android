package com.example.swith.ui.profile

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.swith.R
import com.example.swith.databinding.ActivityProfileModifyBinding
import com.example.swith.databinding.DialogImageBinding
import com.example.swith.databinding.DialogInterestingBinding
import com.example.swith.databinding.DialogProfileBinding
import com.example.swith.ui.dialog.CustomDialog
import com.example.swith.ui.dialog.CustomImageDialog
import com.example.swith.ui.dialog.CustomInterestingDialog
import com.example.swith.ui.study.create.SelectPlaceActivity
import com.example.swith.utils.CustomBinder
import com.example.swith.viewmodel.ProfileModifyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileModifyActivity : AppCompatActivity() {
    private val viewModel by viewModels<ProfileModifyViewModel>()
    private lateinit var binding: ActivityProfileModifyBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //nothing
            } else {
                Toast.makeText(this, "권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                Glide.with(this)
                    .load(it.data!!.data)
                    .into(binding.civProfile)
            }
        }

    private val requestCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                Glide.with(this)
                    .load(it.data!!.extras?.get("data"))
                    .into(binding.civProfile)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_modify)

        initView()
        initListener()
        observe()
    }
    private fun initView() {
//        setShowDimmed(true)
        with(binding){
            lifecycleOwner = this@ProfileModifyActivity
            profileModifyViewModel = viewModel
        }

//        binding.apply {
//            lifecycleOwner = this@ProfileModifyActivity
//            mProfileModifyViewModel =
//                ViewModelProvider(this@ProfileModifyActivity, ProfileModifyViewModel.Factory()).get(
//                    ProfileModifyViewModel::class.java
//                ).apply {
//                    profileModifyViewModel = this
//                    getCurrentProfile().observe(
//                        this@ProfileModifyActivity,
//                        this@ProfileModifyActivity
//                    )
//                    SharedPrefManager(this@ProfileModifyActivity).getLoginData()?.userIdx!!.apply {
//                        requestCurrentProfile(ProfileRequest(this as Long))
//                    }
//            mProfileModifyViewModel?.getCurrentProfileModify()
//                ?.observe(this@ProfileModifyActivity, Observer {
//                    Log.e("doori", "observer = $it")
//                    //TODO 관심분야가 index4번이상부터 안되넹
//                    setShowDimmed(false)
//                    if (it != null) {
//                        if (it!!.isSuccess) {
//                            goProfilePage()
//                        } else {
//                            Toast.makeText(
//                                this@ProfileModifyActivity,
//                                "잠시 후 다시 시작해주세요.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                })

    }
    private fun initListener() {
        with(binding) {
            // 이미지
            civImage.setOnClickListener {
                hideKeyboard()
                showImageDialog()
            }
            // 활동 지역 선택
            tvLocationDetail.setOnClickListener {
                // Todo : 장소 선택 추가
                Intent(this@ProfileModifyActivity, SelectPlaceActivity::class.java).run {
                    this.putExtra("번호", 4)
                    startActivity(this)
                }
                hideKeyboard()
            }
            // 관심 분야 1
            btnInteresting1.setOnClickListener {
                hideKeyboard()
                showInterestingDialog(1)
            }
            // 관심 분야 2
            btnInteresting2.setOnClickListener{
                hideKeyboard()
                showInterestingDialog(2)
            }
            // 완료 버튼
            btnSave.setOnClickListener {
                hideKeyboard()
                if (allCheck()) {
                    showSaveDialog()
                }
            }
        }
    }

    private fun observe(){
        viewModel.interest1.observe(this){
            setInterestButton(binding.btnInteresting1, it)
        }

        viewModel.interest2.observe(this){
            setInterestButton(binding.btnInteresting2, it)
        }

        viewModel.region.observe(this){

        }
    }

    // 관심 분야 버튼
    private fun setInterestButton(button: Button, idx: Int){
        button.apply {
            backgroundTintList = if (idx > 0) ColorStateList.valueOf(resources.getColor(R.color.color_82B4FF, null))
                else ColorStateList.valueOf(resources.getColor(R.color.color_C2DBFF, null))
            text = if (idx > 0) resources.getStringArray(R.array.intersting)[idx] else "+"
        }
    }


    private fun showImageDialog() {
        DataBindingUtil.inflate<DialogImageBinding>(
            LayoutInflater.from(this), R.layout.dialog_image, null, false
        ).apply {
            this.imageDialog = CustomBinder.showCustomImageDialog(this@ProfileModifyActivity,
                root,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                object : CustomImageDialog.DialogClickListener {
                    override fun onClose() {
                        // nothing
                    }

                    override fun onCamera() {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        getImageToCamera()
                    }

                    override fun onGallery() {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        getImageToGallery()
                    }
                })
        }
    }

    private fun getImageToCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).run {
            requestCameraLauncher.launch(this)
        }
    }

    private fun getImageToGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).run {
            type = "image/*"
            requestGalleryLauncher.launch(this)

        }
    }

    private fun showInterestingDialog(btnNumber: Int) {
        val imageArray = resources.getStringArray(R.array.intersting).toList() as ArrayList<String>
        DataBindingUtil.inflate<DialogInterestingBinding>(
            LayoutInflater.from(this),
            R.layout.dialog_interesting,
            null,
            false
        ).apply {
            val interestingDialog = CustomBinder.showCustomInterestringDialog(imageArray,
                viewModel.interest1.value?: 0,
                viewModel.interest2.value?: 0,
                this@ProfileModifyActivity,
                root,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                object : CustomInterestingDialog.DialogClickListener {
                    override fun onSelect(idxSelect: Int) {
                        if (btnNumber == 1) {
                            viewModel.setInterest(true, idxSelect)
                        } else if (btnNumber == 2) {
                            viewModel.setInterest(false, idxSelect)
                        }

                    }

                    override fun onClose() {
                        //nothing
                        Log.e("doori", "onclose")
                    }

                })
            this.interestingDialog = interestingDialog
            val interestingAdapter = interestingDialog.getAdapter()
            this.rvList.adapter = interestingAdapter
        }
    }

    private fun showSaveDialog() {
        DataBindingUtil.inflate<DialogProfileBinding>(
            LayoutInflater.from(this), R.layout.dialog_profile, null, false
        ).apply {
            this.profileDialog = CustomBinder.showCustomDialog(this@ProfileModifyActivity,
                root,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                object : CustomDialog.DialogClickListener {
                    override fun onClose() {
                        // nothing
                    }

                    override fun onConfirm() {
//                        setShowDimmed(true)
//                        requestProfile()
                    }
                })
        }
    }

//    private fun requestProfile() {
//        val email = mProfileModifyViewModel?.getCurrentProfile()?.value?.result!!.email
//
//        binding.apply {
//            Log.e(
//                "doori",
//                "${
//                    ProfileModifyRequest(
//                        email,
//                        getInterestringIndex(btnInteresting1.text.toString()),
//                        getInterestringIndex(btnInteresting2.text.toString()),
//                        etIntroduceDetail.text.toString(),
//                        etNickname.text.toString(),
//                        tvLocationDetail.text.toString()
//                    )
//                }"
//            )
//            mProfileModifyViewModel?.requestCurrentProfileModify(
//                ProfileModifyRequest(
//                    email,
//                    getInterestringIndex(btnInteresting1.text.toString()),
//                    getInterestringIndex(btnInteresting2.text.toString()),
//                    etIntroduceDetail.text.toString(),
//                    etNickname.text.toString(),
//                    tvLocationDetail.text.toString()
//                )
//            )
//        }
//    }

//    fun goProfilePage() {
//        Intent(requireContext(), MainActivity::class.java).run {
//            putExtra("profile", "ProfileFragment")
//            startActivity(this)
//        }
//    }

    fun allCheck(): Boolean {
        binding.apply {
            if (etNickname.text.isEmpty()) {
                showDialog("닉네임을 입력해주세요.")
                return false
            }
            if (btnInteresting1.text.toString() == "+" && btnInteresting2.text.toString() == "+") {
                showDialog("관심분야를 선택해주세요.")
                return false
            }
            if (tvLocationDetail.text.toString() == "지역을 선택해주세요.") {
                showDialog("활동지역을 선택해주세요.")
                return false
            }
            if (etIntroduceDetail.text.isEmpty()) {
                showDialog("소개글을 작성해주세요.")
                return false
            }
            return true
        }
    }

    fun showDialog(errorMsg: String) {
        val builder = AlertDialog.Builder(this).setTitle("프로필을 모두 작성해주세요.")
            .setMessage(errorMsg)
            .setPositiveButton("확인", DialogInterface.OnClickListener { _, _ ->
            }).setNegativeButton("취소", DialogInterface.OnClickListener { _, _ ->

            })
        builder.show()
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = window.currentFocus
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(
                currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

//    override fun onChanged(profileResponse: ProfileResponse?) {
//        setShowDimmed(false)
//        Log.e("doori", "onChanged = ${profileResponse.toString()}")
//        //TODO 관심분야가 없다면?? 널값은 어떻게 처리해줘야할까?
//        profileResponse?.result?.apply {
//            binding.btnInteresting1.text =
//                resources.getStringArray(R.array.intersting)[interestIdx1]
//            binding.btnInteresting2.text =
//                resources.getStringArray(R.array.intersting)[interestIdx2]
//        }
//    }

//    private fun setShowDimmed(isLoading: Boolean) {
//        mProfileModifyViewModel?.apply {
//            if (isLoading) {
//                showLoading()
//            } else {
//                hideLoading()
//            }
//        }
//    }

    private fun getInterestringIndex(interesting: String): Int {
        if (interesting == "자격증/시험") {
            return 1
        } else if (interesting == "어학") {
            return 2
        } else if (interesting == "청소년/입시") {
            return 3
        } else if (interesting == "취업/창업") {
            return 4
        } else if (interesting == "컴퓨터/IT") {
            return 5
        } else if (interesting == "취미/문화") {
            return 6
        } else if (interesting == "면접") {
            return 7
        } else {
            return 8
        }
    }

}



//    override fun onResume() {
//        super.onResume()
//        if (!getSharedPreferences("result4", 0).getString("이름4", "").toString().equals("")) {
//            val shPref1 = getSharedPreferences("result4", 0)
//            val editor1 = getSharedPreferences("result4", 0).edit()
//            binding.tvLocationDetail.text = shPref1.getString("이름4", "")
//        }
//    }


