<template>
  <div class="app-container">
    <el-form ref="form" :model="form" label-width="120">
      <el-form-item label="图片1">
        <el-input v-model="form.url1" />
      </el-form-item>
      <el-form-item label="图片2">
        <el-input v-model="form.url2" />
      </el-form-item>
      <el-row>
        <el-col :span="9">
          <el-form-item>
            <img :src="form.url1" width="200px">
          </el-form-item>
          <el-form-item>
            <img :src="form.url2" width="200px">
          </el-form-item>
        </el-col>
        <el-col :span="9">
          <el-form-item label="">
            <json-viewer
              :value="form.result1"
              :expand-depth="4"
              copyable
              width="400px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item>
        <el-button
          v-loading.fullscreen.lock="fullscreenLoading"
          type="primary"
          size="small"
          element-loading-text="loading"
          @click="onSubmit"
        >提交</el-button>
      </el-form-item>
      <el-form-item>
        <el-divider />
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <div><img :src="form.base64Img1" width="200px" class="avatar"></div>
          <div><img :src="form.base64Img2" width="200px" class="avatar"></div>
          <el-form-item label="本地图片">
            <el-upload
              ref="upload"
              multiple
              name="imageFiles"
              class="upload"
              :on-preview="handlePreview"
              :on-change="handleChange"
              :on-remove="handleRemove"
              :on-exceed="handleExceed"
              :before-upload="beforeUpload"
              :file-list="fileList"
              :http-request="uploadFile"
              ::limit="2"
              :auto-upload="false"
            >
              <el-button slot="trigger" size="small" type="primary">选择图片</el-button>
              <el-button
                v-loading.fullscreen.lock="fullscreenLoading"
                style="margin-left: 10px;"
                type="success"
                size="small"
                element-loading-text="loading"
                @click="submitUpload"
              >上传</el-button>
              <div slot="tip" class="el-upload__tip">图片格式: JPG(JPEG), PNG</div>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="9">
          <el-form-item label="">
            <json-viewer
              :value="form.result2"
              :expand-depth="4"
              copyable
              width="400px"
            />
          </el-form-item>
        </el-col>
      </el-row>

    </el-form>
  </div>
</template>

<script>
import { compareForImageUrls, compareForImageFiles } from '@/api/inference'
import JsonViewer from 'vue-json-viewer'

export default {
  name: 'Feature',
  components: {
    JsonViewer
  },
  data() {
    return {
      fullscreenLoading: false,
      file: [],
      fileList: [], // upload多文件数组 - upload file list
      form: {
        url1: 'https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/train_platform/images/car1.jpg',
        url2: 'https://aias-home.oss-cn-beijing.aliyuncs.com/AIAS/train_platform/images/car2.jpg',
        result1: '',
        result2: '',
        base64Img: ''
      }
    }
  },
  methods: {
    upload() {
      return `${process.env.VUE_APP_BASE_API}/api/inference/compareForImageFiles`
    },
    // 上传文件 - upload file
    uploadFile(param) {
      this.file.push(param.file)
    },
    submitUpload() {
      // this.$refs.upload.submit()
      if (this.fileList.length !== 2) {
        this.$message({
          message: '请选择两张图片',
          type: 'warning'
        })
      } else {
        this.fullscreenLoading = true
        const formData = new FormData() // new formData对象 - new formData object
        this.$refs.upload.submit() // 提交调用uploadFile函数 - submit calls uploadFile function
        this.file.forEach(function(file) { // 遍历上传多个文件 - traverse to upload multiple files
          formData.append('imageFiles', file, file.name)
          // upData.append('file', this.file); //不要直接使用文件数组进行上传，传给后台的是两个Object
          // Do not upload directly using the file array, it is two Objects passed to the background
        })
        compareForImageFiles(formData).then(response => {
          this.form.base64Img1 = response.data.base64Img1
          this.form.base64Img2 = response.data.base64Img2
          this.form.result2 = response.data.result
          this.fullscreenLoading = false
          this.fileList = []
        })
      }
    },
    // 移除
    // Remove
    handleRemove(file, fileList) {
      this.fileList = fileList
      // return this.$confirm(`确定移除 ${ file.name }？`);
    },

    // 选取文件超过数量提示
    // Selecting files exceeds the limit warning
    handleExceed(files, fileList) {
      this.$message.warning(`最多选取两个文件`)
    },
    // 监控上传文件列表
    // Monitor the uploaded file list
    handleChange(file, fileList) {
      const existFile = fileList.slice(0, fileList.length - 1).find(f => f.name === file.name)
      if (existFile) {
        this.$message.error('当前文件已存在!')
        fileList.pop()
      }
      this.fileList = fileList
    },
    handlePreview(file) {
      console.log(file)
    },
    beforeUpload(file) {
      const pass = file.type === 'image/jpg' || 'image/jpeg' || 'image/png'
      if (!pass) {
        this.$message.error('图片格式应该为JPG(JPEG) 或者 PNG!')
      }
      return pass
    },
    onSubmit() {
      this.fullscreenLoading = true
      compareForImageUrls(this.form).then(response => {
        this.fullscreenLoading = false
        this.form.result1 = response.data.result
      })
    }
  }
}
</script>

<style scoped>
  .el-input {
    width: 600px;
  }

  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }

  .line {
    text-align: center;
  }
</style>
