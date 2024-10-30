// MainActivity.kt
package com.example.form

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var addressHelper: AddressHelper
    private lateinit var tvSelectedDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo AddressHelper
        addressHelper = AddressHelper(resources)

        // Lấy view cho các thành phần giao diện
        val etMssv = findViewById<EditText>(R.id.et_mssv)
        val etHoTen = findViewById<EditText>(R.id.et_ho_ten)
        val rgGender = findViewById<RadioGroup>(R.id.rg_gender)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPhone = findViewById<EditText>(R.id.et_phone)
        val spnProvince = findViewById<Spinner>(R.id.spn_province)
        val spnDistrict = findViewById<Spinner>(R.id.spn_district)
        val spnWard = findViewById<Spinner>(R.id.spn_ward)
        val cbAgree = findViewById<CheckBox>(R.id.cb_agree)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)
        tvSelectedDate = findViewById(R.id.tv_selected_date)

        // Xử lý sự kiện cho nút chọn ngày sinh
        findViewById<Button>(R.id.btn_show_date_picker).setOnClickListener {
            showDatePicker()
        }

        // Thiết lập Spinner với dữ liệu tỉnh/thành, quận/huyện, và phường/xã
        setupSpinners(spnProvince, spnDistrict, spnWard)

        // Xử lý sự kiện khi nhấn nút Submit
        btnSubmit.setOnClickListener {
            if (validateInput(etMssv, etHoTen, rgGender, etEmail, etPhone, cbAgree)) {
                Toast.makeText(this, "Thông tin hợp lệ! Đã gửi dữ liệu.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Vui lòng kiểm tra lại các thông tin đã nhập.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hàm hiển thị DatePickerDialog
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Cập nhật TextView với ngày đã chọn
            tvSelectedDate.text = "Ngày sinh: $selectedDay/${selectedMonth + 1}/$selectedYear"
        }, year, month, day)

        datePickerDialog.show()
    }

    // Thiết lập dữ liệu cho các Spinner
    private fun setupSpinners(spnProvince: Spinner, spnDistrict: Spinner, spnWard: Spinner) {
        val provinces = addressHelper.getProvinces()
        val provinceList = listOf("Chọn Tỉnh/Thành phố") + provinces
        spnProvince.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinceList)

        // Khi chọn tỉnh, cập nhật danh sách quận/huyện
        spnProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    spnDistrict.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, listOf("Chọn Quận/Huyện"))
                    spnWard.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, listOf("Chọn Phường/Xã"))
                    return // Nếu chưa chọn tỉnh
                }
                val selectedProvince = provinces[position - 1]
                val districts = addressHelper.getDistricts(selectedProvince)
                val districtList = listOf("Chọn Quận/Huyện") + districts
                spnDistrict.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, districtList)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Khi chọn quận/huyện, cập nhật danh sách phường/xã
        spnDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    spnWard.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, listOf("Chọn Phường/Xã"))
                    return // Nếu chưa chọn quận
                }
                val selectedDistrict = spnDistrict.selectedItem.toString()
                // Đảm bảo bạn đang truyền tên tỉnh vào hàm getWards
                val selectedProvince = spnProvince.selectedItem.toString()
                val wards = addressHelper.getWards(selectedProvince, selectedDistrict)
                val wardList = listOf("Chọn Phường/Xã") + wards
                spnWard.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, wardList)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // Hàm kiểm tra dữ liệu đầu vào
    private fun validateInput(
        etMssv: EditText,
        etHoTen: EditText,
        rgGender: RadioGroup,
        etEmail: EditText,
        etPhone: EditText,
        cbAgree: CheckBox
    ): Boolean {
        return etMssv.text.isNotEmpty() &&
                etHoTen.text.isNotEmpty() &&
                rgGender.checkedRadioButtonId != -1 &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches() &&
                etPhone.text.isNotEmpty() &&
                cbAgree.isChecked
    }
}
