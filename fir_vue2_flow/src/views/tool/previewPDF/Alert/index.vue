<template>
	<!-- ftp-iframe -->
	<div class="all-dialog2 alert">
		<el-dialog v-dialogDrag  v-if="visible" :visible.sync="visible" :width="dialogWidth"
			:close-on-click-modal="false" center @close="titleCancel" :modal-append-to-body='false'>
			<slot></slot>
			<div class="asd" style="width:100%;height:60vh">
				<iframe :src="pdfurl" frameborder="0" style="width:100%;height:100%"></iframe>
			</div>

		</el-dialog>
	</div>
</template>

<script>
	export default {
        // eslint-disable-next-line vue/multi-word-component-names
        name: 'alert',
		data() {
			return {
				value: '',
				defaultProps: {
					children: 'children',
					label: 'label'
				},
				datas: [],
				pdfurl: "",
			}
		},
		props: {

			dialogWidth: {
				type: String,
				default: '47%'
			},


			centerDialogVisibles: {
				type: Boolean,
				default() {
					return true
				}
			},


			isBtLeft: {
				type: Boolean,
				default() {
					return true
				}
			},


			isLoading: {
				type: Boolean,
				default() {
					return false
				}
			},
		},
		computed: {
			// 计算
			visible: {
				get() {
					return this.centerDialogVisibles
				},
			},
		},
		methods: {
			// 事件采用vue父子组件传值
			// 头部关闭按钮
			titleCancel() {
				this.$emit('titlePopupDatas')
			},

			reservePlanDoc(data) {
				const blob = new Blob([data],{type: "application/pdf"});
				this.pdfurl = URL.createObjectURL(blob);
			},

		},
		created() {
		}
	}
</script>

<style scoped>
	.all-dialog2 {
		/*width: 100%;*/
		/*height: 100%;*/
	}

	.el-dialog {
		display: flex;
		flex-direction: column;
		margin: 0 !important;
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		max-height: calc(100% - 30px);
		max-width: calc(100% - 30px);
		/*background: url("~@/assets/images/pdfBackground.png") no-repeat;*/
		background-size: 100% 100%;
		width: 100%;

	}

	/* 字体颜色大小 */
	.el-dialog__title {
		line-height: 1.5rem;
		font-size: 1.125rem;
		position: absolute;
		left: 75px;
		top: 10px;
	}

	.el-select-dropdown__item {
		font-size: 0.875rem;
		padding: 0 1.25rem;
		position: relative;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;

		height: 2.125rem;
		line-height: 2.125rem;
		-webkit-box-sizing: border-box;
		box-sizing: border-box;
		cursor: pointer;
	}

	.el-dialog .el-dialog__body {
		flex: 1;
		overflow: auto;
	}

	.el-dialog__header {
		padding: 1.25rem 1.25rem 0.625rem;
		display: -webkit-box;
	}

	.el-select-dropdown__list {
		background-color: #133864;
	}

	.asds::-webkit-scrollbar {
		width: 100%;
		height: 900px;
		color: #fff;
		overflow-y: auto;
	}

	.asd_1 {
		display: flex;
		justify-content: center;
	}

	.el-dialog__body {
		color: rgb(255, 255, 255);
	}

	/deep/.el-dialog__headerbtn {
		font-size: 2rem;
		color: white;
	}

	/deep/.el-dialog__close {
		font-size: 2rem;
		color: black;
	}
</style>