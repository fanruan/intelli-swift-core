/**
 * @class DataStructureView
 * @extend BI.View
 * 前台数据结构——对比前面版本，在做相关功能的时候可以参照
 */
DataStructureView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(DataStructureView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-data-structure"
        })
    },

    _init: function () {
        DataStructureView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                north: {
                    el: {
                        type: "bi.left_right_vertical_adapt",
                        items: {
                            left: [{
                                type: "bi.label",
                                text: "数据结构",
                                width: 200,
                                cls: "title"
                            }],
                            right: [{
                                type: "bi.button",
                                text: "退出",
                                height: 30,
                                handler: function () {
                                    self.notifyParentEnd();
                                }
                            }]
                        }
                    },
                    height: 50
                },
                center: this._createCenter()
            }
        });
    },

    _createCenter: function () {

        var json = BI.createWidget({
            type: "bi.layout",
            scrolly: true
        });
        json.element.html('<div class="json"><span class="sBrace structure-1" id="s-1">{  </span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-2"><span class="error">createBy</span></span><span class="sColon" id="s-3">:</span><span class="sObjectV" id="s-4">"-999"</span><span class="sComma" id="s-5">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-6"><span class="error">description</span></span><span class="sColon" id="s-7">:</span><span class="sObjectV" id="s-8">""</span><span class="sComma" id="s-9">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-10"><span class="error">edit</span></span><span class="sColon" id="s-11">:</span><span class="sObjectV" id="s-12">"null"</span><span class="sComma" id="s-13">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-14"><span class="error">id</span></span><span class="sColon" id="s-15">:</span><span class="sObjectV" id="s-16">6</span><span class="sComma" id="s-17">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-18"><span class="error">onlyViewAuth</span></span><span class="sColon" id="s-19">:</span><span class="sObjectV" id="s-20">false</span><span class="sComma" id="s-21">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-22"><span class="error">parent</span></span><span class="sColon" id="s-23">:</span><span class="sObjectV" id="s-24">null</span><span class="sComma" id="s-25">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-26"><span class="error">popupConfig</span></span><span class="sColon" id="s-27">:</span><span class="sBrace structure-2" id="s-28">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-29"><span class="error">id</span></span><span class="sColon" id="s-30">:</span><span class="sObjectV" id="s-31">6</span><span class="sComma" id="s-32">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-33"><span class="error">layoutData</span></span><span class="sColon" id="s-34">:</span><span class="sBrace structure-3" id="s-35">{  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-36">}</span><span class="sComma" id="s-37">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-38"><span class="error">layoutType</span></span><span class="sColon" id="s-39">:</span><span class="sObjectV" id="s-40">0</span><span class="sComma" id="s-41">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-42"><span class="error">widgets</span></span><span class="sColon" id="s-43">:</span><span class="sBrace structure-3" id="s-44">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-45">"b8cb9d91f6035364"</span><span class="sColon" id="s-46">:</span><span class="sBrace structure-4" id="s-47">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-48"><span class="error">bounds</span></span><span class="sColon" id="s-49">:</span><span class="sBrace structure-5" id="s-50">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-51"><span class="error">h</span></span><span class="sColon" id="s-52">:</span><span class="sObjectV" id="s-53">250</span><span class="sComma" id="s-54">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-55"><span class="error">w</span></span><span class="sColon" id="s-56">:</span><span class="sObjectV" id="s-57">450</span><span class="sComma" id="s-58">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-59"><span class="error">x</span></span><span class="sColon" id="s-60">:</span><span class="sObjectV" id="s-61">731</span><span class="sComma" id="s-62">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-63"><span class="error">y</span></span><span class="sColon" id="s-64">:</span><span class="sObjectV" id="s-65">50</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-66">}</span><span class="sComma" id="s-67">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-68"><span class="error">dimensions</span></span><span class="sColon" id="s-69">:</span><span class="sBrace structure-5" id="s-70">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-71">"99d9f649ff030d9b"</span><span class="sColon" id="s-72">:</span><span class="sBrace structure-6" id="s-73">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-74"><span class="error">name</span></span><span class="sColon" id="s-75">:</span><span class="sObjectV" id="s-76">"基本ID"</span><span class="sComma" id="s-77">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-78"><span class="error">_src</span></span><span class="sColon" id="s-79">:</span><span class="sBrace structure-7" id="s-80">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-81"><span class="error">field_name</span></span><span class="sColon" id="s-82">:</span><span class="sObjectV" id="s-83">"基本ID"</span><span class="sComma" id="s-84">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-85"><span class="error">field_size</span></span><span class="sColon" id="s-86">:</span><span class="sObjectV" id="s-87">10</span><span class="sComma" id="s-88">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-89"><span class="error">field_type</span></span><span class="sColon" id="s-90">:</span><span class="sObjectV" id="s-91">1</span><span class="sComma" id="s-92">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-93"><span class="error">id</span></span><span class="sColon" id="s-94">:</span><span class="sObjectV" id="s-95">"95d5609a179b4b95"</span><span class="sComma" id="s-96">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-97"><span class="error">is_usable</span></span><span class="sColon" id="s-98">:</span><span class="sObjectV" id="s-99">true</span><span class="sComma" id="s-100">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-101"><span class="error">md5</span></span><span class="sColon" id="s-102">:</span><span class="sObjectV" id="s-103">"e12d12a7b6f36170f1ba85a8311f0311"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-7" id="s-104">}</span><span class="sComma" id="s-105">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-106"><span class="error">type</span></span><span class="sColon" id="s-107">:</span><span class="sObjectV" id="s-108">1</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-109">}</span><span class="sComma" id="s-110">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-111">"d3de2c26f55e8a7c"</span><span class="sColon" id="s-112">:</span><span class="sBrace structure-6" id="s-113">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-114"><span class="error">name</span></span><span class="sColon" id="s-115">:</span><span class="sObjectV" id="s-116">"值"</span><span class="sComma" id="s-117">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-118"><span class="error">_src</span></span><span class="sColon" id="s-119">:</span><span class="sBrace structure-7" id="s-120">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-121"><span class="error">field_name</span></span><span class="sColon" id="s-122">:</span><span class="sObjectV" id="s-123">"值"</span><span class="sComma" id="s-124">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-125"><span class="error">field_size</span></span><span class="sColon" id="s-126">:</span><span class="sObjectV" id="s-127">22</span><span class="sComma" id="s-128">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-129"><span class="error">field_type</span></span><span class="sColon" id="s-130">:</span><span class="sObjectV" id="s-131">2</span><span class="sComma" id="s-132">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-133"><span class="error">id</span></span><span class="sColon" id="s-134">:</span><span class="sObjectV" id="s-135">"95d5609a179b4b95"</span><span class="sComma" id="s-136">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-137"><span class="error">is_usable</span></span><span class="sColon" id="s-138">:</span><span class="sObjectV" id="s-139">true</span><span class="sComma" id="s-140">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-141"><span class="error">md5</span></span><span class="sColon" id="s-142">:</span><span class="sObjectV" id="s-143">"fdd1d2425dd5fdc8bd6059af931da563"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-7" id="s-144">}</span><span class="sComma" id="s-145">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-146"><span class="error">type</span></span><span class="sColon" id="s-147">:</span><span class="sObjectV" id="s-148">2</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-149">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-150">}</span><span class="sComma" id="s-151">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-152"><span class="error">name</span></span><span class="sColon" id="s-153">:</span><span class="sObjectV" id="s-154">"统计组件1"</span><span class="sComma" id="s-155">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-156"><span class="error">type</span></span><span class="sColon" id="s-157">:</span><span class="sObjectV" id="s-158">1</span><span class="sComma" id="s-159">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-160"><span class="error">view</span></span><span class="sColon" id="s-161">:</span><span class="sBrace structure-5" id="s-162">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-163"><span class="error">10000</span></span><span class="sColon" id="s-164"><span class="error">:</span></span><span class="sBracket structure-6" id="s-165">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-166">"99d9f649ff030d9b"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-167">]</span><span class="sComma" id="s-168">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-169"><span class="error">30000</span></span><span class="sColon" id="s-170"><span class="error">:</span></span><span class="sBracket structure-6" id="s-171">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-172">"d3de2c26f55e8a7c"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-173">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-174">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-175">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-176">}</span><br><span>&emsp;&emsp;</span><span class="sBrace structure-2" id="s-177">}</span><span class="sComma" id="s-178">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-179"><span class="error">reg</span></span><span class="sColon" id="s-180">:</span><span class="sBrace structure-2" id="s-181">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-182"><span class="error">hasLic</span></span><span class="sColon" id="s-183">:</span><span class="sObjectV" id="s-184">true</span><span class="sComma" id="s-185">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-186"><span class="error">supportBasic</span></span><span class="sColon" id="s-187">:</span><span class="sObjectV" id="s-188">true</span><span class="sComma" id="s-189">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-190"><span class="error">supportBigData</span></span><span class="sColon" id="s-191">:</span><span class="sObjectV" id="s-192">true</span><span class="sComma" id="s-193">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-194"><span class="error">supportCalculateTarget</span></span><span class="sColon" id="s-195">:</span><span class="sObjectV" id="s-196">true</span><span class="sComma" id="s-197">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-198"><span class="error">supportDatabaseUnion</span></span><span class="sColon" id="s-199">:</span><span class="sObjectV" id="s-200">true</span><span class="sComma" id="s-201">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-202"><span class="error">supportExcelView</span></span><span class="sColon" id="s-203">:</span><span class="sObjectV" id="s-204">true</span><span class="sComma" id="s-205">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-206"><span class="error">supportGeneralControl</span></span><span class="sColon" id="s-207">:</span><span class="sObjectV" id="s-208">true</span><span class="sComma" id="s-209">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-210"><span class="error">supportIncrementUpdate</span></span><span class="sColon" id="s-211">:</span><span class="sObjectV" id="s-212">true</span><span class="sComma" id="s-213">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-214"><span class="error">supportMobileClient</span></span><span class="sColon" id="s-215">:</span><span class="sObjectV" id="s-216">true</span><span class="sComma" id="s-217">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-218"><span class="error">supportMultiStatisticsWidget</span></span><span class="sColon" id="s-219">:</span><span class="sObjectV" id="s-220">true</span><span class="sComma" id="s-221">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-222"><span class="error">supportOLAPTable</span></span><span class="sColon" id="s-223">:</span><span class="sObjectV" id="s-224">true</span><span class="sComma" id="s-225">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-226"><span class="error">supportReportShare</span></span><span class="sColon" id="s-227">:</span><span class="sObjectV" id="s-228">true</span><span class="sComma" id="s-229">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-230"><span class="error">supportSimpleControl</span></span><span class="sColon" id="s-231">:</span><span class="sObjectV" id="s-232">true</span><br><span>&emsp;&emsp;</span><span class="sBrace structure-2" id="s-233">}</span><span class="sComma" id="s-234">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-235"><span class="error">reportName</span></span><span class="sColon" id="s-236">:</span><span class="sObjectV" id="s-237">"axxx"</span><span class="sComma" id="s-238">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-239"><span class="error">rootURL</span></span><span class="sColon" id="s-240">:</span><span class="sObjectV" id="s-241">""</span><span class="sComma" id="s-242">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-243"><span class="error">sessionID</span></span><span class="sColon" id="s-244">:</span><span class="sObjectV" id="s-245">21550</span><span class="sComma" id="s-246">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-247"><span class="error">userId</span></span><span class="sColon" id="s-248">:</span><span class="sObjectV" id="s-249">"-999"</span><br><span class="sBrace structure-1" id="s-250">}</span></div>');

        var tab = BI.createWidget({
            type: "bi.tab",
            tab: {
                height: 30,
                items: [{
                    text: "单指标维度组件",
                    value: 1,
                    cls: "mvc-button layout-bg3"
                },{
                    text: "文本控件 + 组件",
                    value: 2,
                    cls: "mvc-button layout-bg4"
                },{
                    text: "时间控件 + 组件",
                    value: 3,
                    cls: "mvc-button layout-bg4"
                },{
                    text: "明细表",
                    value: 4,
                    cls: "mvc-button layout-bg4"
                },{
                    text: "过滤",
                    value: 5,
                    cls: "mvc-button layout-bg4"
                }]
            },
            cardCreator: BI.bind(this._createTabs, this)
        });
        tab.setSelect(1);

        return BI.createWidget({
            type: "bi.division",
            columns: 2,
            rows: 1,
            items: [{
                column: 0,
                row: 0,
                width: 0.5,
                height: 1,
                el: tab
            }, {
                column: 1,
                row: 0,
                width: 0.5,
                height: 1,
                el: {
                    type: "bi.vertical",
                    items: [{
                        type: "bi.label",
                        height: 30,
                        text: "新版本结构",
                        cls: ""
                    }, json]
                }
            }]
        })
    },

    _createTabs: function(v){
        switch (v){
            case 1:
                var json1 = BI.createWidget({
                    type: "bi.layout",
                    scrolly: true
                });
                json1.element.html('<div class="json"><span class="sBrace structure-1" id="s-1">{ </span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-2">"widget"</span><span class="sColon" id="s-3">:</span><span class="sBracket structure-2" id="s-4">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-5">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-6">"name"</span><span class="sColon" id="s-7">:</span><span class="sObjectV" id="s-8">"统计组件"</span><span class="sComma" id="s-9">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-10">"bounds"</span><span class="sColon" id="s-11">:</span><span class="sBrace structure-4" id="s-12">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-13">"x"</span><span class="sColon" id="s-14">:</span><span class="sObjectV" id="s-15">0</span><span class="sComma" id="s-16">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-17">"y"</span><span class="sColon" id="s-18">:</span><span class="sObjectV" id="s-19">0</span><span class="sComma" id="s-20">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-21">"w"</span><span class="sColon" id="s-22">:</span><span class="sObjectV" id="s-23">1880</span><span class="sComma" id="s-24">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-25">"h"</span><span class="sColon" id="s-26">:</span><span class="sObjectV" id="s-27">888</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-28">}</span><span class="sComma" id="s-29">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-30">"view"</span><span class="sColon" id="s-31">:</span><span class="sBrace structure-4" id="s-32">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-33">"type"</span><span class="sColon" id="s-34">:</span><span class="sObjectV" id="s-35">"0"</span><span class="sComma" id="s-36">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-37">"groups_of_dimensions"</span><span class="sColon" id="s-38">:</span><span class="sBracket structure-5" id="s-39">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-40">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-41">"合同类型"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-42">]</span><span class="sComma" id="s-43">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-44">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-45">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-46">]</span><span class="sComma" id="s-47">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-48">"groups_of_targets"</span><span class="sColon" id="s-49">:</span><span class="sBracket structure-5" id="s-50">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-51">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-52">"购买数量"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-53">]</span><span class="sComma" id="s-54">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-55">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-56">]</span><span class="sComma" id="s-57">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-58">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-59">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-60">]</span><span class="sComma" id="s-61">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-62">"all_of_dimensions"</span><span class="sColon" id="s-63">:</span><span class="sBracket structure-5" id="s-64">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-65">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-66">"合同类型"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-67">]</span><span class="sComma" id="s-68">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-69">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-70">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-71">]</span><span class="sComma" id="s-72">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-73">"all_of_targets"</span><span class="sColon" id="s-74">:</span><span class="sBracket structure-5" id="s-75">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-76">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-77">"购买数量"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-78">]</span><span class="sComma" id="s-79">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-80">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-81">]</span><span class="sComma" id="s-82">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-83">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-84">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-85">]</span><span class="sComma" id="s-86">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-87">"complex_data"</span><span class="sColon" id="s-88">:</span><span class="sBrace structure-5" id="s-89">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-90">"x_view"</span><span class="sColon" id="s-91">:</span><span class="sBracket structure-6" id="s-92">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-93">]</span><span class="sComma" id="s-94">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-95">"y_view"</span><span class="sColon" id="s-96">:</span><span class="sBracket structure-6" id="s-97">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-98">]</span><span class="sComma" id="s-99">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-100">"x_all"</span><span class="sColon" id="s-101">:</span><span class="sBracket structure-6" id="s-102">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-103">]</span><span class="sComma" id="s-104">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-105">"y_all"</span><span class="sColon" id="s-106">:</span><span class="sBracket structure-6" id="s-107">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-108">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-109">}</span><span class="sComma" id="s-110">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-111">"style"</span><span class="sColon" id="s-112">:</span><span class="sBrace structure-5" id="s-113">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-114">"freeze"</span><span class="sColon" id="s-115">:</span><span class="sObjectV" id="s-116">true</span><span class="sComma" id="s-117">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-118">"bind"</span><span class="sColon" id="s-119">:</span><span class="sObjectV" id="s-120">true</span><span class="sComma" id="s-121">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-122">"realdata"</span><span class="sColon" id="s-123">:</span><span class="sObjectV" id="s-124">1</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-125">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-126">}</span><span class="sComma" id="s-127">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-128">"dimensions"</span><span class="sColon" id="s-129">:</span><span class="sBracket structure-4" id="s-130">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-131">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-132">"name"</span><span class="sColon" id="s-133">:</span><span class="sObjectV" id="s-134">"合同类型"</span><span class="sComma" id="s-135">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-136">"type"</span><span class="sColon" id="s-137">:</span><span class="sObjectV" id="s-138">1</span><span class="sComma" id="s-139">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-140">"_src"</span><span class="sColon" id="s-141">:</span><span class="sBrace structure-6" id="s-142">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-143">"connection_name"</span><span class="sColon" id="s-144">:</span><span class="sObjectV" id="s-145">"FRDemo"</span><span class="sComma" id="s-146">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-147">"useable"</span><span class="sColon" id="s-148">:</span><span class="sObjectV" id="s-149">true</span><span class="sComma" id="s-150">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-151">"isSonTable"</span><span class="sColon" id="s-152">:</span><span class="sObjectV" id="s-153">false</span><span class="sComma" id="s-154">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-155">"column_size"</span><span class="sColon" id="s-156">:</span><span class="sObjectV" id="s-157">20</span><span class="sComma" id="s-158">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-159">"table_name_text"</span><span class="sColon" id="s-160">:</span><span class="sObjectV" id="s-161">"合同信息"</span><span class="sComma" id="s-162">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-163">"package_name"</span><span class="sColon" id="s-164">:</span><span class="sObjectV" id="s-165">"业务包111"</span><span class="sComma" id="s-166">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-167">"md5_table_name"</span><span class="sColon" id="s-168">:</span><span class="sObjectV" id="s-169">"e122d4de-2f41-473d-ab48-a3923ea9bf24"</span><span class="sComma" id="s-170">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-171">"py"</span><span class="sColon" id="s-172">:</span><span class="sObjectV" id="s-173">"htlx"</span><span class="sComma" id="s-174">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-175">"table_name"</span><span class="sColon" id="s-176">:</span><span class="sObjectV" id="s-177">"contract"</span><span class="sComma" id="s-178">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-179">"field_type"</span><span class="sColon" id="s-180">:</span><span class="sObjectV" id="s-181">1</span><span class="sComma" id="s-182">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-183">"classType"</span><span class="sColon" id="s-184">:</span><span class="sObjectV" id="s-185">5</span><span class="sComma" id="s-186">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-187">"field_name"</span><span class="sColon" id="s-188">:</span><span class="sObjectV" id="s-189">"合同类型"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-190">}</span><span class="sComma" id="s-191">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-192">"group"</span><span class="sColon" id="s-193">:</span><span class="sBrace structure-6" id="s-194">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-195">"type"</span><span class="sColon" id="s-196">:</span><span class="sObjectV" id="s-197">-2</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-198">}</span><span class="sComma" id="s-199">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-200">"sort"</span><span class="sColon" id="s-201">:</span><span class="sBrace structure-6" id="s-202">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-203">"type"</span><span class="sColon" id="s-204">:</span><span class="sObjectV" id="s-205">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-206">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-207">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-208">]</span><span class="sComma" id="s-209">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-210">"targets"</span><span class="sColon" id="s-211">:</span><span class="sBracket structure-4" id="s-212">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-213">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-214">"summary"</span><span class="sColon" id="s-215">:</span><span class="sObjectV" id="s-216">"sum"</span><span class="sComma" id="s-217">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-218">"style_of_chart"</span><span class="sColon" id="s-219">:</span><span class="sObjectV" id="s-220">0</span><span class="sComma" id="s-221">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-222">"dimension_maps"</span><span class="sColon" id="s-223">:</span><span class="sBracket structure-6" id="s-224">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-7" id="s-225">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-226">"nameOfDimension"</span><span class="sColon" id="s-227">:</span><span class="sObjectV" id="s-228">"合同类型"</span><span class="sComma" id="s-229">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-230">"_src"</span><span class="sColon" id="s-231">:</span><span class="sBrace structure-8" id="s-232">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-233">"connection_name"</span><span class="sColon" id="s-234">:</span><span class="sObjectV" id="s-235">"FRDemo"</span><span class="sComma" id="s-236">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-237">"useable"</span><span class="sColon" id="s-238">:</span><span class="sObjectV" id="s-239">true</span><span class="sComma" id="s-240">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-241">"isSonTable"</span><span class="sColon" id="s-242">:</span><span class="sObjectV" id="s-243">false</span><span class="sComma" id="s-244">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-245">"column_size"</span><span class="sColon" id="s-246">:</span><span class="sObjectV" id="s-247">20</span><span class="sComma" id="s-248">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-249">"table_name_text"</span><span class="sColon" id="s-250">:</span><span class="sObjectV" id="s-251">"合同信息"</span><span class="sComma" id="s-252">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-253">"package_name"</span><span class="sColon" id="s-254">:</span><span class="sObjectV" id="s-255">"业务包111"</span><span class="sComma" id="s-256">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-257">"md5_table_name"</span><span class="sColon" id="s-258">:</span><span class="sObjectV" id="s-259">"e122d4de-2f41-473d-ab48-a3923ea9bf24"</span><span class="sComma" id="s-260">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-261">"py"</span><span class="sColon" id="s-262">:</span><span class="sObjectV" id="s-263">"htlx"</span><span class="sComma" id="s-264">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-265">"table_name"</span><span class="sColon" id="s-266">:</span><span class="sObjectV" id="s-267">"contract"</span><span class="sComma" id="s-268">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-269">"field_type"</span><span class="sColon" id="s-270">:</span><span class="sObjectV" id="s-271">1</span><span class="sComma" id="s-272">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-273">"classType"</span><span class="sColon" id="s-274">:</span><span class="sObjectV" id="s-275">5</span><span class="sComma" id="s-276">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-277">"field_name"</span><span class="sColon" id="s-278">:</span><span class="sObjectV" id="s-279">"合同类型"</span><span class="sComma" id="s-280">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-281">"target_relation"</span><span class="sColon" id="s-282">:</span><span class="sBracket structure-9" id="s-283">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-9" id="s-284">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-8" id="s-285">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-7" id="s-286">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-287">]</span><span class="sComma" id="s-288">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-289">"combi_maps"</span><span class="sColon" id="s-290">:</span><span class="sBracket structure-6" id="s-291">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-292">]</span><span class="sComma" id="s-293">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-294">"type"</span><span class="sColon" id="s-295">:</span><span class="sObjectV" id="s-296">0</span><span class="sComma" id="s-297">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-298">"name"</span><span class="sColon" id="s-299">:</span><span class="sObjectV" id="s-300">"购买数量"</span><span class="sComma" id="s-301">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-302">"_src"</span><span class="sColon" id="s-303">:</span><span class="sBrace structure-6" id="s-304">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-305">"connection_name"</span><span class="sColon" id="s-306">:</span><span class="sObjectV" id="s-307">"FRDemo"</span><span class="sComma" id="s-308">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-309">"table_name"</span><span class="sColon" id="s-310">:</span><span class="sObjectV" id="s-311">"contract"</span><span class="sComma" id="s-312">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-313">"table_name_text"</span><span class="sColon" id="s-314">:</span><span class="sObjectV" id="s-315">"合同信息"</span><span class="sComma" id="s-316">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-317">"field_name"</span><span class="sColon" id="s-318">:</span><span class="sObjectV" id="s-319">"购买数量"</span><span class="sComma" id="s-320">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-321">"md5_table_name"</span><span class="sColon" id="s-322">:</span><span class="sObjectV" id="s-323">"e122d4de-2f41-473d-ab48-a3923ea9bf24"</span><span class="sComma" id="s-324">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-325">"field_type"</span><span class="sColon" id="s-326">:</span><span class="sObjectV" id="s-327">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-328">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-329">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-330">]</span><span class="sComma" id="s-331">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-332">"other_view"</span><span class="sColon" id="s-333">:</span><span class="sBrace structure-4" id="s-334">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-335">"type"</span><span class="sColon" id="s-336">:</span><span class="sObjectV" id="s-337">"暂时省略"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-338">}</span><span class="sComma" id="s-339">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-340">"causes"</span><span class="sColon" id="s-341">:</span><span class="sBracket structure-4" id="s-342">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-343">]</span><span class="sComma" id="s-344">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-345">"results"</span><span class="sColon" id="s-346">:</span><span class="sBracket structure-4" id="s-347">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-348">]</span><span class="sComma" id="s-349">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-350">"translegaldimension"</span><span class="sColon" id="s-351">:</span><span class="sBrace structure-4" id="s-352">{  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-353">}</span><span class="sComma" id="s-354">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-355">"pre_bounds"</span><span class="sColon" id="s-356">:</span><span class="sBrace structure-4" id="s-357">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-358">"x"</span><span class="sColon" id="s-359">:</span><span class="sObjectV" id="s-360">0</span><span class="sComma" id="s-361">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-362">"y"</span><span class="sColon" id="s-363">:</span><span class="sObjectV" id="s-364">0</span><span class="sComma" id="s-365">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-366">"h"</span><span class="sColon" id="s-367">:</span><span class="sObjectV" id="s-368">586</span><span class="sComma" id="s-369">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-370">"w"</span><span class="sColon" id="s-371">:</span><span class="sObjectV" id="s-372">1659</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-373">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-374">}</span><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-375">]</span><span class="sComma" id="s-376">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-377">"control"</span><span class="sColon" id="s-378">:</span><span class="sBracket structure-2" id="s-379">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-380">]</span><span class="sComma" id="s-381">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-382">"detail"</span><span class="sColon" id="s-383">:</span><span class="sBracket structure-2" id="s-384">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-385">]</span><span class="sComma" id="s-386">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-387">"empty_widgets"</span><span class="sColon" id="s-388">:</span><span class="sBracket structure-2" id="s-389">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-390">]</span><span class="sComma" id="s-391">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-392">"layoutType"</span><span class="sColon" id="s-393">:</span><span class="sObjectV" id="s-394">0</span><span class="sComma" id="s-395">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-396">"packageName"</span><span class="sColon" id="s-397">:</span><span class="sObjectV" id="s-398">"业务包111"</span><span class="sComma" id="s-399">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-400">"version"</span><span class="sColon" id="s-401">:</span><span class="sObjectV" id="s-402">3.1</span><span class="sComma" id="s-403">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-404">"layoutData"</span><span class="sColon" id="s-405">:</span><span class="sBrace structure-2" id="s-406">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-407">"width"</span><span class="sColon" id="s-408">:</span><span class="sObjectV" id="s-409">1880</span><span class="sComma" id="s-410">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-411">"height"</span><span class="sColon" id="s-412">:</span><span class="sObjectV" id="s-413">888</span><br><span>&emsp;&emsp;</span><span class="sBrace structure-2" id="s-414">}</span><br><span class="sBrace structure-1" id="s-415">}</span></div>');
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [json1]
                });
            case 2:
                var json2 = BI.createWidget({
                    type: "bi.layout",
                    scrolly: true
                });
                json2.element.html('<div class="json"><span class="sBrace structure-1" id="s-1">{  </span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-2">"widget"</span><span class="sColon" id="s-3">:</span><span class="sBracket structure-2" id="s-4">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-5">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-6">"other_view"</span><span class="sColon" id="s-7">:</span><span class="sBrace structure-4" id="s-8">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-9">"type"</span><span class="sColon" id="s-10">:</span><span class="sObjectV" id="s-11">"暂时省略"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-12">}</span><span class="sComma" id="s-13">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-14">"view"</span><span class="sColon" id="s-15">:</span><span class="sBrace structure-4" id="s-16">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-17">"groups_of_dimensions"</span><span class="sColon" id="s-18">:</span><span class="sBracket structure-5" id="s-19">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-20">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-21">"合同类型"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-22">]</span><span class="sComma" id="s-23">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-24">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-25">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-26">]</span><span class="sComma" id="s-27">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-28">"groups_of_targets"</span><span class="sColon" id="s-29">:</span><span class="sBracket structure-5" id="s-30">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-31">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-32">]</span><span class="sComma" id="s-33">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-34">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-35">]</span><span class="sComma" id="s-36">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-37">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-38">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-39">]</span><span class="sComma" id="s-40">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-41">"all_of_targets"</span><span class="sColon" id="s-42">:</span><span class="sBracket structure-5" id="s-43">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-44">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-45">]</span><span class="sComma" id="s-46">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-47">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-48">]</span><span class="sComma" id="s-49">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-50">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-51">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-52">]</span><span class="sComma" id="s-53">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-54">"all_of_dimensions"</span><span class="sColon" id="s-55">:</span><span class="sBracket structure-5" id="s-56">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-57">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-58">"合同类型"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-59">]</span><span class="sComma" id="s-60">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-61">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-62">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-63">]</span><span class="sComma" id="s-64">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-65">"style"</span><span class="sColon" id="s-66">:</span><span class="sBrace structure-5" id="s-67">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-68">"freeze"</span><span class="sColon" id="s-69">:</span><span class="sObjectV" id="s-70">true</span><span class="sComma" id="s-71">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-72">"bind"</span><span class="sColon" id="s-73">:</span><span class="sObjectV" id="s-74">true</span><span class="sComma" id="s-75">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-76">"tableStyle"</span><span class="sColon" id="s-77">:</span><span class="sObjectV" id="s-78">1</span><span class="sComma" id="s-79">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-80">"realdata"</span><span class="sColon" id="s-81">:</span><span class="sObjectV" id="s-82">1</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-83">}</span><span class="sComma" id="s-84">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-85">"type"</span><span class="sColon" id="s-86">:</span><span class="sObjectV" id="s-87">"0"</span><span class="sComma" id="s-88">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-89">"complex_data"</span><span class="sColon" id="s-90">:</span><span class="sBrace structure-5" id="s-91">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-92">"y_view"</span><span class="sColon" id="s-93">:</span><span class="sBracket structure-6" id="s-94">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-95">]</span><span class="sComma" id="s-96">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-97">"x_view"</span><span class="sColon" id="s-98">:</span><span class="sBracket structure-6" id="s-99">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-100">]</span><span class="sComma" id="s-101">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-102">"x_all"</span><span class="sColon" id="s-103">:</span><span class="sBracket structure-6" id="s-104">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-105">]</span><span class="sComma" id="s-106">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-107">"y_all"</span><span class="sColon" id="s-108">:</span><span class="sBracket structure-6" id="s-109">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-110">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-111">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-112">}</span><span class="sComma" id="s-113">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-114">"drill_target"</span><span class="sColon" id="s-115">:</span><span class="sBracket structure-4" id="s-116">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-117">]</span><span class="sComma" id="s-118">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-119">"self_drill"</span><span class="sColon" id="s-120">:</span><span class="sBrace structure-4" id="s-121">{  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-122">}</span><span class="sComma" id="s-123">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-124">"translegaldimension"</span><span class="sColon" id="s-125">:</span><span class="sBrace structure-4" id="s-126">{  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-127">}</span><span class="sComma" id="s-128">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-129">"causes"</span><span class="sColon" id="s-130">:</span><span class="sBracket structure-4" id="s-131">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-132">]</span><span class="sComma" id="s-133">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-134">"name"</span><span class="sColon" id="s-135">:</span><span class="sObjectV" id="s-136">"统计组件"</span><span class="sComma" id="s-137">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-138">"bounds"</span><span class="sColon" id="s-139">:</span><span class="sBrace structure-4" id="s-140">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-141">"w"</span><span class="sColon" id="s-142">:</span><span class="sObjectV" id="s-143">454</span><span class="sComma" id="s-144">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-145">"x"</span><span class="sColon" id="s-146">:</span><span class="sObjectV" id="s-147">794</span><span class="sComma" id="s-148">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-149">"h"</span><span class="sColon" id="s-150">:</span><span class="sObjectV" id="s-151">304</span><span class="sComma" id="s-152">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-153">"y"</span><span class="sColon" id="s-154">:</span><span class="sObjectV" id="s-155">88</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-156">}</span><span class="sComma" id="s-157">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-158">"targets"</span><span class="sColon" id="s-159">:</span><span class="sBracket structure-4" id="s-160">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-161">]</span><span class="sComma" id="s-162">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-163">"results"</span><span class="sColon" id="s-164">:</span><span class="sBracket structure-4" id="s-165">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-166">]</span><span class="sComma" id="s-167">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-168">"pre_bounds"</span><span class="sColon" id="s-169">:</span><span class="sBrace structure-4" id="s-170">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-171">"x"</span><span class="sColon" id="s-172">:</span><span class="sObjectV" id="s-173">0</span><span class="sComma" id="s-174">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-175">"y"</span><span class="sColon" id="s-176">:</span><span class="sObjectV" id="s-177">0</span><span class="sComma" id="s-178">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-179">"h"</span><span class="sColon" id="s-180">:</span><span class="sObjectV" id="s-181">586</span><span class="sComma" id="s-182">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-183">"w"</span><span class="sColon" id="s-184">:</span><span class="sObjectV" id="s-185">1659</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-186">}</span><span class="sComma" id="s-187">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-188">"dimensions"</span><span class="sColon" id="s-189">:</span><span class="sBracket structure-4" id="s-190">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-191">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-192">"name"</span><span class="sColon" id="s-193">:</span><span class="sObjectV" id="s-194">"合同类型"</span><span class="sComma" id="s-195">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-196">"_src"</span><span class="sColon" id="s-197">:</span><span class="sBrace structure-6" id="s-198">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-199">"connection_name"</span><span class="sColon" id="s-200">:</span><span class="sObjectV" id="s-201">"FRDemo"</span><span class="sComma" id="s-202">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-203">"useable"</span><span class="sColon" id="s-204">:</span><span class="sObjectV" id="s-205">true</span><span class="sComma" id="s-206">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-207">"isSonTable"</span><span class="sColon" id="s-208">:</span><span class="sObjectV" id="s-209">false</span><span class="sComma" id="s-210">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-211">"column_size"</span><span class="sColon" id="s-212">:</span><span class="sObjectV" id="s-213">20</span><span class="sComma" id="s-214">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-215">"table_name_text"</span><span class="sColon" id="s-216">:</span><span class="sObjectV" id="s-217">"合同信息"</span><span class="sComma" id="s-218">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-219">"package_name"</span><span class="sColon" id="s-220">:</span><span class="sObjectV" id="s-221">"业务包111"</span><span class="sComma" id="s-222">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-223">"md5_table_name"</span><span class="sColon" id="s-224">:</span><span class="sObjectV" id="s-225">"e122d4de-2f41-473d-ab48-a3923ea9bf24"</span><span class="sComma" id="s-226">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-227">"py"</span><span class="sColon" id="s-228">:</span><span class="sObjectV" id="s-229">"htlx"</span><span class="sComma" id="s-230">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-231">"table_name"</span><span class="sColon" id="s-232">:</span><span class="sObjectV" id="s-233">"contract"</span><span class="sComma" id="s-234">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-235">"field_type"</span><span class="sColon" id="s-236">:</span><span class="sObjectV" id="s-237">1</span><span class="sComma" id="s-238">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-239">"classType"</span><span class="sColon" id="s-240">:</span><span class="sObjectV" id="s-241">5</span><span class="sComma" id="s-242">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-243">"field_name"</span><span class="sColon" id="s-244">:</span><span class="sObjectV" id="s-245">"合同类型"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-246">}</span><span class="sComma" id="s-247">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-248">"sort"</span><span class="sColon" id="s-249">:</span><span class="sBrace structure-6" id="s-250">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-251">"type"</span><span class="sColon" id="s-252">:</span><span class="sObjectV" id="s-253">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-254">}</span><span class="sComma" id="s-255">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-256">"type"</span><span class="sColon" id="s-257">:</span><span class="sObjectV" id="s-258">1</span><span class="sComma" id="s-259">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-260">"group"</span><span class="sColon" id="s-261">:</span><span class="sBrace structure-6" id="s-262">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-263">"type"</span><span class="sColon" id="s-264">:</span><span class="sObjectV" id="s-265">-2</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-266">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-267">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-268">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-269">}</span><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-270">]</span><span class="sComma" id="s-271">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-272">"control"</span><span class="sColon" id="s-273">:</span><span class="sBracket structure-2" id="s-274">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-275">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-276">"name"</span><span class="sColon" id="s-277">:</span><span class="sObjectV" id="s-278">"文本控件"</span><span class="sComma" id="s-279">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-280">"bounds"</span><span class="sColon" id="s-281">:</span><span class="sBrace structure-4" id="s-282">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-283">"w"</span><span class="sColon" id="s-284">:</span><span class="sObjectV" id="s-285">252</span><span class="sComma" id="s-286">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-287">"x"</span><span class="sColon" id="s-288">:</span><span class="sObjectV" id="s-289">99</span><span class="sComma" id="s-290">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-291">"h"</span><span class="sColon" id="s-292">:</span><span class="sObjectV" id="s-293">57</span><span class="sComma" id="s-294">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-295">"y"</span><span class="sColon" id="s-296">:</span><span class="sObjectV" id="s-297">125</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-298">}</span><span class="sComma" id="s-299">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-300">"rank"</span><span class="sColon" id="s-301">:</span><span class="sObjectV" id="s-302">0</span><span class="sComma" id="s-303">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-304">"field_value"</span><span class="sColon" id="s-305">:</span><span class="sBracket structure-4" id="s-306">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-307">"长期协议"</span><span class="sComma" id="s-308">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-309">"长期协议订单"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-310">]</span><span class="sComma" id="s-311">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-312">"type"</span><span class="sColon" id="s-313">:</span><span class="sObjectV" id="s-314">6</span><span class="sComma" id="s-315">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-316">"config"</span><span class="sColon" id="s-317">:</span><span class="sBracket structure-4" id="s-318">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-319">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-320">"table_name_text"</span><span class="sColon" id="s-321">:</span><span class="sObjectV" id="s-322">"合同信息"</span><span class="sComma" id="s-323">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-324">"target_relation"</span><span class="sColon" id="s-325">:</span><span class="sBracket structure-6" id="s-326">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-327">]</span><span class="sComma" id="s-328">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-329">"md5_table_name"</span><span class="sColon" id="s-330">:</span><span class="sObjectV" id="s-331">"e122d4de-2f41-473d-ab48-a3923ea9bf24"</span><span class="sComma" id="s-332">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-333">"py"</span><span class="sColon" id="s-334">:</span><span class="sObjectV" id="s-335">"htlx"</span><span class="sComma" id="s-336">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-337">"table_name"</span><span class="sColon" id="s-338">:</span><span class="sObjectV" id="s-339">"contract"</span><span class="sComma" id="s-340">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-341">"field_name"</span><span class="sColon" id="s-342">:</span><span class="sObjectV" id="s-343">"合同类型"</span><span class="sComma" id="s-344">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-345">"connection_name"</span><span class="sColon" id="s-346">:</span><span class="sObjectV" id="s-347">"FRDemo"</span><span class="sComma" id="s-348">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-349">"useable"</span><span class="sColon" id="s-350">:</span><span class="sObjectV" id="s-351">true</span><span class="sComma" id="s-352">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-353">"isSonTable"</span><span class="sColon" id="s-354">:</span><span class="sObjectV" id="s-355">false</span><span class="sComma" id="s-356">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-357">"column_size"</span><span class="sColon" id="s-358">:</span><span class="sObjectV" id="s-359">20</span><span class="sComma" id="s-360">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-361">"package_name"</span><span class="sColon" id="s-362">:</span><span class="sObjectV" id="s-363">"业务包111"</span><span class="sComma" id="s-364">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-365">"field_type"</span><span class="sColon" id="s-366">:</span><span class="sObjectV" id="s-367">1</span><span class="sComma" id="s-368">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-369">"classType"</span><span class="sColon" id="s-370">:</span><span class="sObjectV" id="s-371">5</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-372">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-373">]</span><span class="sComma" id="s-374">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-375">"value"</span><span class="sColon" id="s-376">:</span><span class="sBracket structure-4" id="s-377">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-378">"长期协议"</span><span class="sComma" id="s-379">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-380">"长期协议订单"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-381">]</span><span class="sComma" id="s-382">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-383">"pre_bounds"</span><span class="sColon" id="s-384">:</span><span class="sBrace structure-4" id="s-385">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-386">"w"</span><span class="sColon" id="s-387">:</span><span class="sObjectV" id="s-388">1659</span><span class="sComma" id="s-389">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-390">"x"</span><span class="sColon" id="s-391">:</span><span class="sObjectV" id="s-392">0</span><span class="sComma" id="s-393">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-394">"h"</span><span class="sColon" id="s-395">:</span><span class="sObjectV" id="s-396">649</span><span class="sComma" id="s-397">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-398">"y"</span><span class="sColon" id="s-399">:</span><span class="sObjectV" id="s-400">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-401">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-402">}</span><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-403">]</span><span class="sComma" id="s-404">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-405">"detail"</span><span class="sColon" id="s-406">:</span><span class="sBracket structure-2" id="s-407">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-408">]</span><span class="sComma" id="s-409">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-410">"empty_widgets"</span><span class="sColon" id="s-411">:</span><span class="sBracket structure-2" id="s-412">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-413">]</span><span class="sComma" id="s-414">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-415">"layoutType"</span><span class="sColon" id="s-416">:</span><span class="sObjectV" id="s-417">1</span><span class="sComma" id="s-418">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-419">"packageName"</span><span class="sColon" id="s-420">:</span><span class="sObjectV" id="s-421">"业务包111"</span><span class="sComma" id="s-422">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-423">"version"</span><span class="sColon" id="s-424">:</span><span class="sObjectV" id="s-425">3.1</span><br><span class="sBrace structure-1" id="s-426">}</span></div>');
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [json2]
                });
            case 3:
                var json3 = BI.createWidget({
                    type: "bi.layout",
                    scrolly: true
                });
                json3.element.html('<div class="json"><span class="sBrace structure-1" id="s-1">{  </span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-2">"widget"</span><span class="sColon" id="s-3">:</span><span class="sBracket structure-2" id="s-4">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-5">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-6">"other_view"</span><span class="sColon" id="s-7">:</span><span class="sBrace structure-4" id="s-8">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-9">"type"</span><span class="sColon" id="s-10">:</span><span class="sObjectV" id="s-11">"暂时省略"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-12">}</span><span class="sComma" id="s-13">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-14">"view"</span><span class="sColon" id="s-15">:</span><span class="sBrace structure-4" id="s-16">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-17">"groups_of_dimensions"</span><span class="sColon" id="s-18">:</span><span class="sBracket structure-5" id="s-19">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-20">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-21">"日期(注册时间)"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-22">]</span><span class="sComma" id="s-23">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-24">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-25">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-26">]</span><span class="sComma" id="s-27">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-28">"groups_of_targets"</span><span class="sColon" id="s-29">:</span><span class="sBracket structure-5" id="s-30">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-31">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-32">]</span><span class="sComma" id="s-33">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-34">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-35">]</span><span class="sComma" id="s-36">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-37">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-38">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-39">]</span><span class="sComma" id="s-40">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-41">"all_of_targets"</span><span class="sColon" id="s-42">:</span><span class="sBracket structure-5" id="s-43">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-44">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-45">]</span><span class="sComma" id="s-46">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-47">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-48">]</span><span class="sComma" id="s-49">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-50">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-51">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-52">]</span><span class="sComma" id="s-53">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-54">"all_of_dimensions"</span><span class="sColon" id="s-55">:</span><span class="sBracket structure-5" id="s-56">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-57">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-58">"日期(注册时间)"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-59">]</span><span class="sComma" id="s-60">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-61">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-62">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-63">]</span><span class="sComma" id="s-64">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-65">"style"</span><span class="sColon" id="s-66">:</span><span class="sBrace structure-5" id="s-67">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-68">"freeze"</span><span class="sColon" id="s-69">:</span><span class="sObjectV" id="s-70">true</span><span class="sComma" id="s-71">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-72">"bind"</span><span class="sColon" id="s-73">:</span><span class="sObjectV" id="s-74">true</span><span class="sComma" id="s-75">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-76">"tableStyle"</span><span class="sColon" id="s-77">:</span><span class="sObjectV" id="s-78">1</span><span class="sComma" id="s-79">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-80">"realdata"</span><span class="sColon" id="s-81">:</span><span class="sObjectV" id="s-82">1</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-83">}</span><span class="sComma" id="s-84">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-85">"type"</span><span class="sColon" id="s-86">:</span><span class="sObjectV" id="s-87">"0"</span><span class="sComma" id="s-88">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-89">"complex_data"</span><span class="sColon" id="s-90">:</span><span class="sBrace structure-5" id="s-91">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-92">"y_view"</span><span class="sColon" id="s-93">:</span><span class="sBracket structure-6" id="s-94">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-95">]</span><span class="sComma" id="s-96">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-97">"x_view"</span><span class="sColon" id="s-98">:</span><span class="sBracket structure-6" id="s-99">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-100">]</span><span class="sComma" id="s-101">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-102">"x_all"</span><span class="sColon" id="s-103">:</span><span class="sBracket structure-6" id="s-104">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-105">]</span><span class="sComma" id="s-106">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-107">"y_all"</span><span class="sColon" id="s-108">:</span><span class="sBracket structure-6" id="s-109">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-110">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-111">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-112">}</span><span class="sComma" id="s-113">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-114">"drill_target"</span><span class="sColon" id="s-115">:</span><span class="sBracket structure-4" id="s-116">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-117">]</span><span class="sComma" id="s-118">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-119">"self_drill"</span><span class="sColon" id="s-120">:</span><span class="sBrace structure-4" id="s-121">{  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-122">}</span><span class="sComma" id="s-123">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-124">"translegaldimension"</span><span class="sColon" id="s-125">:</span><span class="sBrace structure-4" id="s-126">{  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-127">}</span><span class="sComma" id="s-128">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-129">"causes"</span><span class="sColon" id="s-130">:</span><span class="sBracket structure-4" id="s-131">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-132">]</span><span class="sComma" id="s-133">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-134">"name"</span><span class="sColon" id="s-135">:</span><span class="sObjectV" id="s-136">"统计组件"</span><span class="sComma" id="s-137">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-138">"bounds"</span><span class="sColon" id="s-139">:</span><span class="sBrace structure-4" id="s-140">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-141">"w"</span><span class="sColon" id="s-142">:</span><span class="sObjectV" id="s-143">454</span><span class="sComma" id="s-144">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-145">"x"</span><span class="sColon" id="s-146">:</span><span class="sObjectV" id="s-147">869</span><span class="sComma" id="s-148">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-149">"h"</span><span class="sColon" id="s-150">:</span><span class="sObjectV" id="s-151">304</span><span class="sComma" id="s-152">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-153">"y"</span><span class="sColon" id="s-154">:</span><span class="sObjectV" id="s-155">163</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-156">}</span><span class="sComma" id="s-157">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-158">"targets"</span><span class="sColon" id="s-159">:</span><span class="sBracket structure-4" id="s-160">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-161">]</span><span class="sComma" id="s-162">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-163">"results"</span><span class="sColon" id="s-164">:</span><span class="sBracket structure-4" id="s-165">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-166">]</span><span class="sComma" id="s-167">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-168">"pre_bounds"</span><span class="sColon" id="s-169">:</span><span class="sBrace structure-4" id="s-170">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-171">"w"</span><span class="sColon" id="s-172">:</span><span class="sObjectV" id="s-173">1659</span><span class="sComma" id="s-174">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-175">"x"</span><span class="sColon" id="s-176">:</span><span class="sObjectV" id="s-177">0</span><span class="sComma" id="s-178">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-179">"h"</span><span class="sColon" id="s-180">:</span><span class="sObjectV" id="s-181">586</span><span class="sComma" id="s-182">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-183">"y"</span><span class="sColon" id="s-184">:</span><span class="sObjectV" id="s-185">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-186">}</span><span class="sComma" id="s-187">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-188">"dimensions"</span><span class="sColon" id="s-189">:</span><span class="sBracket structure-4" id="s-190">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-191">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-192">"name"</span><span class="sColon" id="s-193">:</span><span class="sObjectV" id="s-194">"日期(注册时间)"</span><span class="sComma" id="s-195">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-196">"_src"</span><span class="sColon" id="s-197">:</span><span class="sBrace structure-6" id="s-198">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-199">"table_name_text"</span><span class="sColon" id="s-200">:</span><span class="sObjectV" id="s-201">"合同信息"</span><span class="sComma" id="s-202">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-203">"md5_table_name"</span><span class="sColon" id="s-204">:</span><span class="sObjectV" id="s-205">"e122d4de-2f41-473d-ab48-a3923ea9bf24"</span><span class="sComma" id="s-206">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-207">"py"</span><span class="sColon" id="s-208">:</span><span class="sObjectV" id="s-209">"zcsj"</span><span class="sComma" id="s-210">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-211">"table_name"</span><span class="sColon" id="s-212">:</span><span class="sObjectV" id="s-213">"contract"</span><span class="sComma" id="s-214">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-215">"field_name"</span><span class="sColon" id="s-216">:</span><span class="sObjectV" id="s-217">"注册时间"</span><span class="sComma" id="s-218">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-219">"connection_name"</span><span class="sColon" id="s-220">:</span><span class="sObjectV" id="s-221">"FRDemo"</span><span class="sComma" id="s-222">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-223">"useable"</span><span class="sColon" id="s-224">:</span><span class="sObjectV" id="s-225">true</span><span class="sComma" id="s-226">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-227">"isSonTable"</span><span class="sColon" id="s-228">:</span><span class="sObjectV" id="s-229">false</span><span class="sComma" id="s-230">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-231">"default_name"</span><span class="sColon" id="s-232">:</span><span class="sObjectV" id="s-233">"日期(注册时间)"</span><span class="sComma" id="s-234">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-235">"column_size"</span><span class="sColon" id="s-236">:</span><span class="sObjectV" id="s-237">10</span><span class="sComma" id="s-238">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-239">"package_name"</span><span class="sColon" id="s-240">:</span><span class="sObjectV" id="s-241">"业务包111"</span><span class="sComma" id="s-242">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-243">"field_type"</span><span class="sColon" id="s-244">:</span><span class="sObjectV" id="s-245">3</span><span class="sComma" id="s-246">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-247">"classType"</span><span class="sColon" id="s-248">:</span><span class="sObjectV" id="s-249">4</span><span class="sComma" id="s-250">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-251">"group"</span><span class="sColon" id="s-252">:</span><span class="sObjectV" id="s-253">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-254">}</span><span class="sComma" id="s-255">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-256">"sort"</span><span class="sColon" id="s-257">:</span><span class="sBrace structure-6" id="s-258">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-259">"type"</span><span class="sColon" id="s-260">:</span><span class="sObjectV" id="s-261">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-262">}</span><span class="sComma" id="s-263">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-264">"type"</span><span class="sColon" id="s-265">:</span><span class="sObjectV" id="s-266">3</span><span class="sComma" id="s-267">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-268">"group"</span><span class="sColon" id="s-269">:</span><span class="sObjectV" id="s-270">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-271">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-272">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-273">}</span><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-274">]</span><span class="sComma" id="s-275">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-276">"control"</span><span class="sColon" id="s-277">:</span><span class="sBracket structure-2" id="s-278">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-279">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-280">"name"</span><span class="sColon" id="s-281">:</span><span class="sObjectV" id="s-282">"年份控件"</span><span class="sComma" id="s-283">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-284">"bounds"</span><span class="sColon" id="s-285">:</span><span class="sBrace structure-4" id="s-286">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-287">"w"</span><span class="sColon" id="s-288">:</span><span class="sObjectV" id="s-289">252</span><span class="sComma" id="s-290">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-291">"x"</span><span class="sColon" id="s-292">:</span><span class="sObjectV" id="s-293">119</span><span class="sComma" id="s-294">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-295">"h"</span><span class="sColon" id="s-296">:</span><span class="sObjectV" id="s-297">57</span><span class="sComma" id="s-298">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-299">"y"</span><span class="sColon" id="s-300">:</span><span class="sObjectV" id="s-301">190</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-302">}</span><span class="sComma" id="s-303">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-304">"rank"</span><span class="sColon" id="s-305">:</span><span class="sObjectV" id="s-306">0</span><span class="sComma" id="s-307">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-308">"field_value"</span><span class="sColon" id="s-309">:</span><span class="sBrace structure-4" id="s-310">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-311">"year"</span><span class="sColon" id="s-312">:</span><span class="sObjectV" id="s-313">2013</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-314">}</span><span class="sComma" id="s-315">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-316">"type"</span><span class="sColon" id="s-317">:</span><span class="sObjectV" id="s-318">1</span><span class="sComma" id="s-319">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-320">"config"</span><span class="sColon" id="s-321">:</span><span class="sBracket structure-4" id="s-322">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-323">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-324">"table_name_text"</span><span class="sColon" id="s-325">:</span><span class="sObjectV" id="s-326">"合同信息"</span><span class="sComma" id="s-327">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-328">"target_relation"</span><span class="sColon" id="s-329">:</span><span class="sBracket structure-6" id="s-330">[  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-6" id="s-331">]</span><span class="sComma" id="s-332">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-333">"md5_table_name"</span><span class="sColon" id="s-334">:</span><span class="sObjectV" id="s-335">"e122d4de-2f41-473d-ab48-a3923ea9bf24"</span><span class="sComma" id="s-336">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-337">"py"</span><span class="sColon" id="s-338">:</span><span class="sObjectV" id="s-339">"zcsj"</span><span class="sComma" id="s-340">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-341">"table_name"</span><span class="sColon" id="s-342">:</span><span class="sObjectV" id="s-343">"contract"</span><span class="sComma" id="s-344">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-345">"field_name"</span><span class="sColon" id="s-346">:</span><span class="sObjectV" id="s-347">"注册时间"</span><span class="sComma" id="s-348">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-349">"connection_name"</span><span class="sColon" id="s-350">:</span><span class="sObjectV" id="s-351">"FRDemo"</span><span class="sComma" id="s-352">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-353">"useable"</span><span class="sColon" id="s-354">:</span><span class="sObjectV" id="s-355">true</span><span class="sComma" id="s-356">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-357">"isSonTable"</span><span class="sColon" id="s-358">:</span><span class="sObjectV" id="s-359">false</span><span class="sComma" id="s-360">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-361">"column_size"</span><span class="sColon" id="s-362">:</span><span class="sObjectV" id="s-363">10</span><span class="sComma" id="s-364">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-365">"package_name"</span><span class="sColon" id="s-366">:</span><span class="sObjectV" id="s-367">"业务包111"</span><span class="sComma" id="s-368">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-369">"field_type"</span><span class="sColon" id="s-370">:</span><span class="sObjectV" id="s-371">3</span><span class="sComma" id="s-372">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-373">"classType"</span><span class="sColon" id="s-374">:</span><span class="sObjectV" id="s-375">4</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-376">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-377">]</span><span class="sComma" id="s-378">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-379">"value"</span><span class="sColon" id="s-380">:</span><span class="sBrace structure-4" id="s-381">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-382">"year"</span><span class="sColon" id="s-383">:</span><span class="sObjectV" id="s-384">2013</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-385">}</span><span class="sComma" id="s-386">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-387">"pre_bounds"</span><span class="sColon" id="s-388">:</span><span class="sBrace structure-4" id="s-389">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-390">"w"</span><span class="sColon" id="s-391">:</span><span class="sObjectV" id="s-392">1659</span><span class="sComma" id="s-393">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-394">"x"</span><span class="sColon" id="s-395">:</span><span class="sObjectV" id="s-396">0</span><span class="sComma" id="s-397">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-398">"h"</span><span class="sColon" id="s-399">:</span><span class="sObjectV" id="s-400">649</span><span class="sComma" id="s-401">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-402">"y"</span><span class="sColon" id="s-403">:</span><span class="sObjectV" id="s-404">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-405">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-406">}</span><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-407">]</span><span class="sComma" id="s-408">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-409">"detail"</span><span class="sColon" id="s-410">:</span><span class="sBracket structure-2" id="s-411">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-412">]</span><span class="sComma" id="s-413">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-414">"empty_widgets"</span><span class="sColon" id="s-415">:</span><span class="sBracket structure-2" id="s-416">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-417">]</span><span class="sComma" id="s-418">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-419">"layoutType"</span><span class="sColon" id="s-420">:</span><span class="sObjectV" id="s-421">1</span><span class="sComma" id="s-422">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-423">"packageName"</span><span class="sColon" id="s-424">:</span><span class="sObjectV" id="s-425">"业务包111"</span><span class="sComma" id="s-426">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-427">"version"</span><span class="sColon" id="s-428">:</span><span class="sObjectV" id="s-429">3.1</span><br><span class="sBrace structure-1" id="s-430">}</span></div>');
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [json3]
                });
            case 4:
                var json4 = BI.createWidget({
                    type: "bi.layout",
                    scrolly: true
                });
                json4.element.html('<div class="json"><span class="sBrace structure-1" id="s-1">{  </span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-2">"widget"</span><span class="sColon" id="s-3">:</span><span class="sBracket structure-2" id="s-4">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-5">]</span><span class="sComma" id="s-6">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-7">"control"</span><span class="sColon" id="s-8">:</span><span class="sBracket structure-2" id="s-9">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-10">]</span><span class="sComma" id="s-11">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-12">"detail"</span><span class="sColon" id="s-13">:</span><span class="sBracket structure-2" id="s-14">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-15">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-16">"name"</span><span class="sColon" id="s-17">:</span><span class="sObjectV" id="s-18">"明细表"</span><span class="sComma" id="s-19">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-20">"bounds"</span><span class="sColon" id="s-21">:</span><span class="sBrace structure-4" id="s-22">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-23">"x"</span><span class="sColon" id="s-24">:</span><span class="sObjectV" id="s-25">481</span><span class="sComma" id="s-26">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-27">"y"</span><span class="sColon" id="s-28">:</span><span class="sObjectV" id="s-29">137</span><span class="sComma" id="s-30">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-31">"w"</span><span class="sColon" id="s-32">:</span><span class="sObjectV" id="s-33">454</span><span class="sComma" id="s-34">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-35">"h"</span><span class="sColon" id="s-36">:</span><span class="sObjectV" id="s-37">304</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-38">}</span><span class="sComma" id="s-39">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-40">"view"</span><span class="sColon" id="s-41">:</span><span class="sBrace structure-4" id="s-42">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-43">"type"</span><span class="sColon" id="s-44">:</span><span class="sObjectV" id="s-45">8</span><span class="sComma" id="s-46">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-47">"dimensions"</span><span class="sColon" id="s-48">:</span><span class="sBracket structure-5" id="s-49">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-50">"合同类型"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-51">]</span><span class="sComma" id="s-52">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-53">"style"</span><span class="sColon" id="s-54">:</span><span class="sBrace structure-5" id="s-55">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-56">"freeze"</span><span class="sColon" id="s-57">:</span><span class="sObjectV" id="s-58">true</span><span class="sComma" id="s-59">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-60">"number"</span><span class="sColon" id="s-61">:</span><span class="sObjectV" id="s-62">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-63">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-64">}</span><span class="sComma" id="s-65">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-66">"details"</span><span class="sColon" id="s-67">:</span><span class="sBracket structure-4" id="s-68">[  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-69">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-70">"name"</span><span class="sColon" id="s-71">:</span><span class="sObjectV" id="s-72">"合同类型"</span><span class="sComma" id="s-73">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-74">"type"</span><span class="sColon" id="s-75">:</span><span class="sObjectV" id="s-76">1</span><span class="sComma" id="s-77">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-78">"_src"</span><span class="sColon" id="s-79">:</span><span class="sBrace structure-6" id="s-80">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-81">"connection_name"</span><span class="sColon" id="s-82">:</span><span class="sObjectV" id="s-83">"FRDemo"</span><span class="sComma" id="s-84">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-85">"useable"</span><span class="sColon" id="s-86">:</span><span class="sObjectV" id="s-87">true</span><span class="sComma" id="s-88">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-89">"isSonTable"</span><span class="sColon" id="s-90">:</span><span class="sObjectV" id="s-91">false</span><span class="sComma" id="s-92">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-93">"column_size"</span><span class="sColon" id="s-94">:</span><span class="sObjectV" id="s-95">20</span><span class="sComma" id="s-96">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-97">"table_name_text"</span><span class="sColon" id="s-98">:</span><span class="sObjectV" id="s-99">"合同信息"</span><span class="sComma" id="s-100">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-101">"package_name"</span><span class="sColon" id="s-102">:</span><span class="sObjectV" id="s-103">"业务包111"</span><span class="sComma" id="s-104">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-105">"md5_table_name"</span><span class="sColon" id="s-106">:</span><span class="sObjectV" id="s-107">"e122d4de-2f41-473d-ab48-a3923ea9bf24"</span><span class="sComma" id="s-108">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-109">"py"</span><span class="sColon" id="s-110">:</span><span class="sObjectV" id="s-111">"htlx"</span><span class="sComma" id="s-112">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-113">"table_name"</span><span class="sColon" id="s-114">:</span><span class="sObjectV" id="s-115">"contract"</span><span class="sComma" id="s-116">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-117">"field_type"</span><span class="sColon" id="s-118">:</span><span class="sObjectV" id="s-119">1</span><span class="sComma" id="s-120">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-121">"classType"</span><span class="sColon" id="s-122">:</span><span class="sObjectV" id="s-123">5</span><span class="sComma" id="s-124">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-125">"field_name"</span><span class="sColon" id="s-126">:</span><span class="sObjectV" id="s-127">"合同类型"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-128">}</span><span class="sComma" id="s-129">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-130">"group"</span><span class="sColon" id="s-131">:</span><span class="sBrace structure-6" id="s-132">{  </span><br><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-133">}</span><span class="sComma" id="s-134">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-135">"isdetail"</span><span class="sColon" id="s-136">:</span><span class="sObjectV" id="s-137">true</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-5" id="s-138">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-4" id="s-139">]</span><span class="sComma" id="s-140">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-141">"pre_bounds"</span><span class="sColon" id="s-142">:</span><span class="sBrace structure-4" id="s-143">{  </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-144">"w"</span><span class="sColon" id="s-145">:</span><span class="sObjectV" id="s-146">1659</span><span class="sComma" id="s-147">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-148">"h"</span><span class="sColon" id="s-149">:</span><span class="sObjectV" id="s-150">649</span><span class="sComma" id="s-151">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-152">"y"</span><span class="sColon" id="s-153">:</span><span class="sObjectV" id="s-154">0</span><span class="sComma" id="s-155">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-156">"x"</span><span class="sColon" id="s-157">:</span><span class="sObjectV" id="s-158">0</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-159">}</span><span class="sComma" id="s-160">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-161">"page"</span><span class="sColon" id="s-162">:</span><span class="sObjectV" id="s-163">2</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-3" id="s-164">}</span><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-165">]</span><span class="sComma" id="s-166">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-167">"empty_widgets"</span><span class="sColon" id="s-168">:</span><span class="sBracket structure-2" id="s-169">[  </span><br><br><span>&emsp;&emsp;</span><span class="sBracket structure-2" id="s-170">]</span><span class="sComma" id="s-171">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-172">"layoutType"</span><span class="sColon" id="s-173">:</span><span class="sObjectV" id="s-174">1</span><span class="sComma" id="s-175">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-176">"packageName"</span><span class="sColon" id="s-177">:</span><span class="sObjectV" id="s-178">"业务包111"</span><span class="sComma" id="s-179">,</span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-180">"version"</span><span class="sColon" id="s-181">:</span><span class="sObjectV" id="s-182">3.1</span><br><span class="sBrace structure-1" id="s-183">}</span></div>');
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [json4]
                });
            case 5:
                var json5 = BI.createWidget({
                    type: "bi.layout",
                    scrolly: true
                });
                json5.element.html('<div class="json"><span class="sBrace structure-1" id="s-1">{ </span><br><span>&emsp;&emsp;</span><span class="sObjectK" id="s-2">"result_filter"</span><span class="sColon" id="s-3">:</span><span class="sBrace structure-2" id="s-4">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-5">"andor"</span><span class="sColon" id="s-6">:</span><span class="sObjectV" id="s-7">"or"</span><span class="sComma" id="s-8">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-9">"condition"</span><span class="sColon" id="s-10">:</span><span class="sBracket structure-3" id="s-11">[ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-12">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-13">"andor"</span><span class="sColon" id="s-14">:</span><span class="sObjectV" id="s-15">"and"</span><span class="sComma" id="s-16">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-17">"condition"</span><span class="sColon" id="s-18">:</span><span class="sBracket structure-5" id="s-19">[ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-20">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-21">"type"</span><span class="sColon" id="s-22">:</span><span class="sObjectV" id="s-23">10</span><span class="sComma" id="s-24">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-25">"value"</span><span class="sColon" id="s-26">:</span><span class="sBracket structure-7" id="s-27">[ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-28">"长期协议"</span><span class="sComma" id="s-29">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-30">"服务协议"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-7" id="s-31">]</span><span class="sComma" id="s-32">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-33">"isDimensionSelf"</span><span class="sColon" id="s-34">:</span><span class="sObjectV" id="s-35">true</span><span class="sComma" id="s-36">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-37">"target_name"</span><span class="sColon" id="s-38">:</span><span class="sObjectV" id="s-39">""</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-40">}</span><span class="sComma" id="s-41">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-42">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-43">"type"</span><span class="sColon" id="s-44">:</span><span class="sObjectV" id="s-45">12</span><span class="sComma" id="s-46">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-47">"value"</span><span class="sColon" id="s-48">:</span><span class="sBracket structure-7" id="s-49">[ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sArrayV" id="s-50">"长期协议"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-7" id="s-51">]</span><span class="sComma" id="s-52">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-53">"isDimensionSelf"</span><span class="sColon" id="s-54">:</span><span class="sObjectV" id="s-55">true</span><span class="sComma" id="s-56">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-57">"target_name"</span><span class="sColon" id="s-58">:</span><span class="sObjectV" id="s-59">""</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-60">}</span><span class="sComma" id="s-61">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-62">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-63">"type"</span><span class="sColon" id="s-64">:</span><span class="sObjectV" id="s-65">5</span><span class="sComma" id="s-66">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-67">"value"</span><span class="sColon" id="s-68">:</span><span class="sObjectV" id="s-69">""</span><span class="sComma" id="s-70">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-71">"isDimensionSelf"</span><span class="sColon" id="s-72">:</span><span class="sObjectV" id="s-73">true</span><span class="sComma" id="s-74">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-75">"target_name"</span><span class="sColon" id="s-76">:</span><span class="sObjectV" id="s-77">""</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-78">}</span><span class="sComma" id="s-79">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-80">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-81">"expression"</span><span class="sColon" id="s-82">:</span><span class="sBracket structure-7" id="s-83">[ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-8" id="s-84">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-85">"isBlock"</span><span class="sColon" id="s-86">:</span><span class="sObjectV" id="s-87">true</span><span class="sComma" id="s-88">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-89">"value"</span><span class="sColon" id="s-90">:</span><span class="sObjectV" id="s-91">"购买数量"</span><span class="sComma" id="s-92">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-93">"showName"</span><span class="sColon" id="s-94">:</span><span class="sObjectV" id="s-95">"购买数量"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-8" id="s-96">}</span><span class="sComma" id="s-97">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-8" id="s-98">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-99">"isBlock"</span><span class="sColon" id="s-100">:</span><span class="sObjectV" id="s-101">false</span><span class="sComma" id="s-102">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-103">"value"</span><span class="sColon" id="s-104">:</span><span class="sObjectV" id="s-105">"&nbsp;&amp;lt;&nbsp;40"</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-8" id="s-106">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-7" id="s-107">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-6" id="s-108">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-5" id="s-109">]</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-110">}</span><span class="sComma" id="s-111">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-112">{ </span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-113">"type"</span><span class="sColon" id="s-114">:</span><span class="sObjectV" id="s-115">2</span><span class="sComma" id="s-116">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-117">"value"</span><span class="sColon" id="s-118">:</span><span class="sObjectV" id="s-119">"协议"</span><span class="sComma" id="s-120">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-121">"isDimensionSelf"</span><span class="sColon" id="s-122">:</span><span class="sObjectV" id="s-123">true</span><span class="sComma" id="s-124">,</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sObjectK" id="s-125">"target_name"</span><span class="sColon" id="s-126">:</span><span class="sObjectV" id="s-127">""</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBrace structure-4" id="s-128">}</span><br><span>&emsp;&emsp;</span><span>&emsp;&emsp;</span><span class="sBracket structure-3" id="s-129">]</span><br><span>&emsp;&emsp;</span><span class="sBrace structure-2" id="s-130">}</span><br><span class="sBrace structure-1" id="s-131">}</span></div>');
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [json5]
                });

        }
    }
});

DataStructureModel = BI.inherit(BI.Model, {
    _static: function () {
        return {
            //之前版本的数据结构
            pre : {
                widget: [
                    {
                        other_view: {
                            //其他视图配置属性……
                        },
                        view: {
                            groups_of_dimensions: [
                                ["合同ID"], ["客户ID"]
                            ],
                            groups_of_targets: [
                                ["购买数量", "合同金额所示"], [], []
                            ],
                            all_of_targets: [
                                ["购买数量", "合同金额所示"], [], []
                            ],
                            all_of_dimensions: [
                                ["合同ID"], ["客户ID"]
                            ],
                            style: {
                                freeze: true,
                                bind: true,
                                tableStyle: 2,
                                realdata: 1
                            },
                            type: 0,
                            complex_data: {
                                y_view: [],
                                x_view: [],
                                x_all: [],
                                y_all: []
                            }
                        },
                        drill_target: [],
                        self_drill: {},
                        translegaldimension: {},
                        causes: [],
                        name: "统计组件",
                        bounds: {w: 1863, x: 0, h: 444, y: 444},
                        targets: [
                            {
                                summary: "sum",
                                style_of_chart: 0,
                                name: "购买数量",
                                _src: {
                                    connection_name: "FRDemo",
                                    table_name_text: "合同信息",
                                    md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    table_name: "contract",
                                    field_type: 0,
                                    field_name: "购买数量"
                                },
                                dimension_maps: [
                                    {
                                        nameOfDimension: "合同ID",
                                        _src: {
                                            table_name_text: "合同信息",
                                            target_relation: [],
                                            md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                            py: "htID",
                                            table_name: "contract",
                                            field_name: "合同ID",
                                            connection_name: "FRDemo",
                                            useable: true,
                                            isSonTable: false,
                                            column_size: 200,
                                            package_name: "业务包111",
                                            field_type: 1,
                                            classType: 5
                                        }
                                    }, {
                                        nameOfDimension: "客户ID",
                                        _src: {
                                            table_name_text: "合同信息",
                                            target_relation: [],
                                            md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                            py: "khID",
                                            table_name: "contract",
                                            field_name: "客户ID",
                                            connection_name: "FRDemo",
                                            useable: true,
                                            isSonTable: false,
                                            column_size: 200,
                                            package_name: "业务包111",
                                            field_type: 1,
                                            classType: 5
                                        }
                                    }
                                ],
                                type: 0,
                                combi_maps: []
                            }, {
                                summary: "sum",
                                style_of_chart: 0,
                                name: "合同金额所示",
                                _src: {
                                    connection_name: "FRDemo",
                                    field_name_text: "合同金额所示",
                                    table_name_text: "合同信息",
                                    md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    table_name: "contract",
                                    field_type: 0,
                                    field_name: "总金额"
                                },
                                dimension_maps: [
                                    {
                                        nameOfDimension: "合同ID",
                                        _src: {
                                            table_name_text: "合同信息",
                                            target_relation: [],
                                            md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                            py: "htID",
                                            table_name: "contract",
                                            field_name: "合同ID",
                                            connection_name: "FRDemo",
                                            useable: true,
                                            isSonTable: false,
                                            column_size: 200,
                                            package_name: "业务包111",
                                            field_type: 1,
                                            classType: 5
                                        }
                                    }, {
                                        nameOfDimension: "客户ID",
                                        _src: {
                                            table_name_text: "合同信息",
                                            target_relation: [],
                                            md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                            py: "khID",
                                            table_name: "contract",
                                            field_name: "客户ID",
                                            connection_name: "FRDemo",
                                            useable: true,
                                            isSonTable: false,
                                            column_size: 200,
                                            package_name: "业务包111",
                                            field_type: 1,
                                            classType: 5
                                        }
                                    }
                                ],
                                type: 0,
                                combi_maps: []
                            }
                        ],
                        results: [],
                        pre_bounds: {w: 1659, x: 0, h: 29, y: 0},
                        dimensions: [
                            {
                                name: "合同ID",
                                _src: {
                                    connection_name: "FRDemo",
                                    useable: true,
                                    isSonTable: false,
                                    column_size: 200,
                                    table_name_text: "合同信息",
                                    package_name: "业务包111",
                                    md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    py: "htID",
                                    table_name: "contract",
                                    field_type: 1,
                                    classType: 5,
                                    field_name: "合同ID"
                                },
                                sort: {
                                    type: 0
                                },
                                type: 1,
                                group: {
                                    type: -2
                                }
                            }, {
                                name: "客户ID",
                                _src: {
                                    connection_name: "FRDemo",
                                    useable: true,
                                    isSonTable: false,
                                    column_size: 200,
                                    table_name_text: "合同信息",
                                    package_name: "业务包111",
                                    md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    py: "khID",
                                    table_name: "contract",
                                    field_type: 1,
                                    classType: 5,
                                    field_name: "客户ID"
                                },
                                sort: {
                                    type: 0
                                },
                                type: 1,
                                group: {
                                    type: -2
                                }
                            }
                        ]
                    }
                ],
                control: [
                    {
                        name: "文本控件",
                        bounds: {w: 1863, x: 0, h: 57, y: 0},
                        rank: 0,
                        type: 6,
                        config: [
                            {
                                table_name_text: "合同信息",
                                target_relation: [],
                                md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                py: "htlx",
                                table_name: "contract",
                                field_name: "合同类型",
                                connection_name: "FRDemo",
                                useable: true,
                                isSonTable: false,
                                column_size: 20,
                                package_name: "业务包111",
                                field_type: 1,
                                classType: 5
                            }
                        ],
                        pre_bounds: {w: 1659, x: 0, h: 92, y: 0}
                    }
                ],
                detail: [
                    {
                        view: {
                            style: {
                                number: 0,
                                freeze: true
                            },
                            type: 8,
                            dimensions: ["合同类型", "购买数量"]
                        },
                        drill_target: [],
                        name: "明细表",
                        bounds: {w: 1863, x: 0, h: 387, y: 57},
                        details: [
                            {
                                name: "合同类型",
                                _src: {
                                    connection_name: "FRDemo",
                                    useable: true,
                                    isSonTable: false,
                                    column_size: 20,
                                    table_name_text: "合同信息",
                                    package_name: "业务包111",
                                    md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    py: "htlx",
                                    table_name: "contract",
                                    field_type: 1,
                                    classType: 5,
                                    field_name: "合同类型"
                                },
                                isdetail: true,
                                type: 1,
                                group: {}
                            }, {
                                name: "购买数量",
                                _src: {
                                    connection_name: "FRDemo",
                                    useable: true,
                                    isSonTable: false,
                                    column_size: 10,
                                    table_name_text: "合同信息",
                                    package_name: "业务包111",
                                    md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    py: "gmsl",
                                    table_name: "contract",
                                    field_type: 0,
                                    classType: 1,
                                    field_name: "购买数量"
                                },
                                isdetail: true,
                                type: 0,
                                group: {},
                                conditions: {
                                    andor: "and",
                                    condition: [
                                        {
                                            id: 1442216619787,
                                            type: 0,
                                            field_type: 0,
                                            _src: {
                                                connection_name: "FRDemo",
                                                useable: true,
                                                isSonTable: false,
                                                column_size: 10,
                                                table_name_text: "合同信息",
                                                md5_table_name: "e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                                py: "gmsl",
                                                table_name: "contract",
                                                field_type: 0,
                                                classType: 1,
                                                field_name: "购买数量",
                                                target_relation: []
                                            },
                                            value: {
                                                min: 20,
                                                closemin: false,
                                                max: 50,
                                                closemax: false
                                            }
                                        }
                                    ]
                                }
                            }
                        ],
                        page: 2,
                        pre_bounds: {w: 1659, h: 235, y: 0, x: 0}
                    }
                ],
                empty_widgets: [],
                layoutType: BI.Arrangement.LAYOUT_TYPE.FREE,
                packageName: "业务包111",
                version: 3.1,
                layoutData: {
                    width: 1863,
                    height: 888
                }
            },
            //3.6版本仅包含单个维度和指标的组件
            preWidget1: {
                "widget":[
                    {
                        "name":"统计组件",
                        "bounds":{
                            "x":0,
                            "y":0,
                            "w":1880,
                            "h":888
                        },
                        "view":{
                            "type":"0",
                            "groups_of_dimensions":[
                                [
                                    "合同类型"
                                ],
                                [

                                ]
                            ],
                            "groups_of_targets":[
                                [
                                    "购买数量"
                                ],
                                [

                                ],
                                [

                                ]
                            ],
                            "all_of_dimensions":[
                                [
                                    "合同类型"
                                ],
                                [

                                ]
                            ],
                            "all_of_targets":[
                                [
                                    "购买数量"
                                ],
                                [

                                ],
                                [

                                ]
                            ],
                            "complex_data":{
                                "x_view":[

                                ],
                                "y_view":[

                                ],
                                "x_all":[

                                ],
                                "y_all":[

                                ]
                            },
                            "style":{
                                "freeze":true,
                                "bind":true,
                                "realdata":1
                            }
                        },
                        "dimensions":[
                            {
                                "name":"合同类型",
                                "type":1,
                                "_src":{
                                    "connection_name":"FRDemo",
                                    "useable":true,
                                    "isSonTable":false,
                                    "column_size":20,
                                    "table_name_text":"合同信息",
                                    "package_name":"业务包111",
                                    "md5_table_name":"e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    "py":"htlx",
                                    "table_name":"contract",
                                    "field_type":1,
                                    "classType":5,
                                    "field_name":"合同类型"
                                },
                                "group":{
                                    "type":-2
                                },
                                "sort":{
                                    "type":0
                                }
                            }
                        ],
                        "targets":[
                            {
                                "summary":"sum",
                                "style_of_chart":0,
                                "dimension_maps":[
                                    {
                                        "nameOfDimension":"合同类型",
                                        "_src":{
                                            "connection_name":"FRDemo",
                                            "useable":true,
                                            "isSonTable":false,
                                            "column_size":20,
                                            "table_name_text":"合同信息",
                                            "package_name":"业务包111",
                                            "md5_table_name":"e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                            "py":"htlx",
                                            "table_name":"contract",
                                            "field_type":1,
                                            "classType":5,
                                            "field_name":"合同类型",
                                            "target_relation":[

                                            ]
                                        }
                                    }
                                ],
                                "combi_maps":[

                                ],
                                "type":0,
                                "name":"购买数量",
                                "_src":{
                                    "connection_name":"FRDemo",
                                    "table_name":"contract",
                                    "table_name_text":"合同信息",
                                    "field_name":"购买数量",
                                    "md5_table_name":"e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    "field_type":0
                                }
                            }
                        ],
                        "other_view":{
                            "type": "暂时省略"
                        },
                        "causes":[

                        ],
                        "results":[

                        ],
                        "translegaldimension":{

                        },
                        "pre_bounds":{
                            "x":0,
                            "y":0,
                            "h":586,
                            "w":1659
                        }
                    }
                ],
                "control":[

                ],
                "detail":[

                ],
                "empty_widgets":[

                ],
                "layoutType":0,
                "packageName":"业务包111",
                "version":3.1,
                "layoutData":{
                    "width":1880,
                    "height":888
                }
            },
            //3.6版本仅包含一个组件和一个文本控件
            preWidget2: {
                "widget":[
                    {
                        "other_view":{
                            "type": "暂时省略"
                        },
                        "view":{
                            "groups_of_dimensions":[
                                [
                                    "合同类型"
                                ],
                                [

                                ]
                            ],
                            "groups_of_targets":[
                                [

                                ],
                                [

                                ],
                                [

                                ]
                            ],
                            "all_of_targets":[
                                [

                                ],
                                [

                                ],
                                [

                                ]
                            ],
                            "all_of_dimensions":[
                                [
                                    "合同类型"
                                ],
                                [

                                ]
                            ],
                            "style":{
                                "freeze":true,
                                "bind":true,
                                "tableStyle":1,
                                "realdata":1
                            },
                            "type":"0",
                            "complex_data":{
                                "y_view":[

                                ],
                                "x_view":[

                                ],
                                "x_all":[

                                ],
                                "y_all":[

                                ]
                            }
                        },
                        "drill_target":[

                        ],
                        "self_drill":{

                        },
                        "translegaldimension":{

                        },
                        "causes":[

                        ],
                        "name":"统计组件",
                        "bounds":{
                            "w":454,
                            "x":794,
                            "h":304,
                            "y":88
                        },
                        "targets":[

                        ],
                        "results":[

                        ],
                        "pre_bounds":{
                            "x":0,
                            "y":0,
                            "h":586,
                            "w":1659
                        },
                        "dimensions":[
                            {
                                "name":"合同类型",
                                "_src":{
                                    "connection_name":"FRDemo",
                                    "useable":true,
                                    "isSonTable":false,
                                    "column_size":20,
                                    "table_name_text":"合同信息",
                                    "package_name":"业务包111",
                                    "md5_table_name":"e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    "py":"htlx",
                                    "table_name":"contract",
                                    "field_type":1,
                                    "classType":5,
                                    "field_name":"合同类型"
                                },
                                "sort":{
                                    "type":0
                                },
                                "type":1,
                                "group":{
                                    "type":-2
                                }
                            }
                        ]
                    }
                ],
                "control":[
                    {
                        "name":"文本控件",
                        "bounds":{
                            "w":252,
                            "x":99,
                            "h":57,
                            "y":125
                        },
                        "rank":0,
                        "field_value":[
                            "长期协议",
                            "长期协议订单"
                        ],
                        "type":6,
                        "config":[
                            {
                                "table_name_text":"合同信息",
                                "target_relation":[

                                ],
                                "md5_table_name":"e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                "py":"htlx",
                                "table_name":"contract",
                                "field_name":"合同类型",
                                "connection_name":"FRDemo",
                                "useable":true,
                                "isSonTable":false,
                                "column_size":20,
                                "package_name":"业务包111",
                                "field_type":1,
                                "classType":5
                            }
                        ],
                        "value":[
                            "长期协议",
                            "长期协议订单"
                        ],
                        "pre_bounds":{
                            "w":1659,
                            "x":0,
                            "h":649,
                            "y":0
                        }
                    }
                ],
                "detail":[

                ],
                "empty_widgets":[

                ],
                "layoutType":1,
                "packageName":"业务包111",
                "version":3.1
            },
            //3.6版本仅包含日期（年份）控件和组件
            preWidget3: {
                "widget":[
                    {
                        "other_view":{
                            "type":"暂时省略"
                        },
                        "view":{
                            "groups_of_dimensions":[
                                [
                                    "日期(注册时间)"
                                ],
                                [

                                ]
                            ],
                            "groups_of_targets":[
                                [

                                ],
                                [

                                ],
                                [

                                ]
                            ],
                            "all_of_targets":[
                                [

                                ],
                                [

                                ],
                                [

                                ]
                            ],
                            "all_of_dimensions":[
                                [
                                    "日期(注册时间)"
                                ],
                                [

                                ]
                            ],
                            "style":{
                                "freeze":true,
                                "bind":true,
                                "tableStyle":1,
                                "realdata":1
                            },
                            "type":"0",
                            "complex_data":{
                                "y_view":[

                                ],
                                "x_view":[

                                ],
                                "x_all":[

                                ],
                                "y_all":[

                                ]
                            }
                        },
                        "drill_target":[

                        ],
                        "self_drill":{

                        },
                        "translegaldimension":{

                        },
                        "causes":[

                        ],
                        "name":"统计组件",
                        "bounds":{
                            "w":454,
                            "x":869,
                            "h":304,
                            "y":163
                        },
                        "targets":[

                        ],
                        "results":[

                        ],
                        "pre_bounds":{
                            "w":1659,
                            "x":0,
                            "h":586,
                            "y":0
                        },
                        "dimensions":[
                            {
                                "name":"日期(注册时间)",
                                "_src":{
                                    "table_name_text":"合同信息",
                                    "md5_table_name":"e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    "py":"zcsj",
                                    "table_name":"contract",
                                    "field_name":"注册时间",
                                    "connection_name":"FRDemo",
                                    "useable":true,
                                    "isSonTable":false,
                                    "default_name":"日期(注册时间)",
                                    "column_size":10,
                                    "package_name":"业务包111",
                                    "field_type":3,
                                    "classType":4,
                                    "group":0
                                },
                                "sort":{
                                    "type":0
                                },
                                "type":3,
                                "group":0
                            }
                        ]
                    }
                ],
                "control":[
                    {
                        "name":"年份控件",
                        "bounds":{
                            "w":252,
                            "x":119,
                            "h":57,
                            "y":190
                        },
                        "rank":0,
                        "field_value":{
                            "year":2013
                        },
                        "type":1,
                        "config":[
                            {
                                "table_name_text":"合同信息",
                                "target_relation":[

                                ],
                                "md5_table_name":"e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                "py":"zcsj",
                                "table_name":"contract",
                                "field_name":"注册时间",
                                "connection_name":"FRDemo",
                                "useable":true,
                                "isSonTable":false,
                                "column_size":10,
                                "package_name":"业务包111",
                                "field_type":3,
                                "classType":4
                            }
                        ],
                        "value":{
                            "year":2013
                        },
                        "pre_bounds":{
                            "w":1659,
                            "x":0,
                            "h":649,
                            "y":0
                        }
                    }
                ],
                "detail":[

                ],
                "empty_widgets":[

                ],
                "layoutType":1,
                "packageName":"业务包111",
                "version":3.1
            },
            //3.6版本明细表
            preWidget4: {
                "widget":[

                ],
                "control":[

                ],
                "detail":[
                    {
                        "name":"明细表",
                        "bounds":{
                            "x":481,
                            "y":137,
                            "w":454,
                            "h":304
                        },
                        "view":{
                            "type":8,
                            "dimensions":[
                                "合同类型"
                            ],
                            "style":{
                                "freeze":true,
                                "number":0
                            }
                        },
                        "details":[
                            {
                                "name":"合同类型",
                                "type":1,
                                "_src":{
                                    "connection_name":"FRDemo",
                                    "useable":true,
                                    "isSonTable":false,
                                    "column_size":20,
                                    "table_name_text":"合同信息",
                                    "package_name":"业务包111",
                                    "md5_table_name":"e122d4de-2f41-473d-ab48-a3923ea9bf24",
                                    "py":"htlx",
                                    "table_name":"contract",
                                    "field_type":1,
                                    "classType":5,
                                    "field_name":"合同类型"
                                },
                                "group":{

                                },
                                "isdetail":true
                            }
                        ],
                        "pre_bounds":{
                            "w":1659,
                            "h":649,
                            "y":0,
                            "x":0
                        },
                        "page":2
                    }
                ],
                "empty_widgets":[

                ],
                "layoutType":1,
                "packageName":"业务包111",
                "version":3.1
            },
            //3.6版本过滤
            preWidget5: {
                "result_filter":{
                    "andor":"or",
                    "condition":[
                        {
                            "andor":"and",
                            "condition":[
                                {
                                    "type":10,
                                    "value":[
                                        "长期协议",
                                        "服务协议"
                                    ],
                                    "isDimensionSelf":true,
                                    "target_name":""
                                },
                                {
                                    "type":12,
                                    "value":[
                                        "长期协议"
                                    ],
                                    "isDimensionSelf":true,
                                    "target_name":""
                                },
                                {
                                    "type":5,
                                    "value":"",
                                    "isDimensionSelf":true,
                                    "target_name":""
                                },
                                {
                                    "expression":[
                                        {
                                            "isBlock":true,
                                            "value":"购买数量",
                                            "showName":"购买数量"
                                        },
                                        {
                                            "isBlock":false,
                                            "value":" &lt; 40"
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            "type":2,
                            "value":"协议",
                            "isDimensionSelf":true,
                            "target_name":""
                        }
                    ]
                }
            },

            current : {
                createBy: "-999",		//创建者ID
                description: ""	,		//描述
                edit: "null",		//编辑/查看状态
                id: 6,		//模板ID，创建模板的时候自动生成
                onlyViewAuth: false,
                parent: null,
                popupConfig: {
                    id: 6,		//模板ID，同上
                    layoutData: {},		//布局数据
                    layoutType: BI.Arrangement.LAYOUT_TYPE.FREE,	//布局方式--自由/自适应，BICst.DASHBOARD_LAYOUT_ARRAY
                    widgets: {
                        "b8cb9d91f6035364": {
                            bounds: {		//位置信息
                                h: 250,
                                w: 450,
                                x: 731,
                                y: 50
                            },
                            dimensions: {
                                "99d9f649ff030d9b": {
                                    name: "基本ID",		//名称
                                    _src: {	//字段信息
                                        field_name: "基本ID",		//字段名称
                                        field_size: 10,		//字段长度
                                        field_type: 1,		//字段类型
                                        id: "95d5609a179b4b95",		//字段id
                                        is_usable: true,		//是否使用
                                        md5: "e12d12a7b6f36170f1ba85a8311f0311" 		//所在表MD5值
                                    },
                                    type: 1		//类型
                                },
                                "d3de2c26f55e8a7c": {		//某个维度或指标
                                    name: "值",		//名称
                                    _src: {		//字段信息
                                        field_name: "值",		//字段名称
                                        field_size: 22,		//字段长度
                                        field_type: 2,		//字段类型
                                        id: "95d5609a179b4b95",	//字段id
                                        is_usable: true,		//是否使用
                                        md5: "fdd1d2425dd5fdc8bd6059af931da563"		//所在表MD5值
                                    },
                                    type: 2		//类型
                                }
                            },
                            name: "统计组件1",		//组件名称
                            type: 1,		//图表类型
                            view: {		//指标维度区域--一般共五个区域，10000、20000~50000表示改区域的key，value为当前区域中所存在的维度或指标的key数组
                                10000: ["99d9f649ff030d9b"],	//当前表示在行表头区域中存在一个key为99d9f649ff030d9b（对应于上面的dimensions中的key）的维度
                                30000: ["d3de2c26f55e8a7c"]
                            }
                            //type和view的常量信息详细见于model.detail.js中的定义
                        }
                    }
                },
                reg: {		//注册信息，用于版本权限的控制
                    hasLic: true,
                    supportBasic: true,
                    supportBigData: true,
                    supportCalculateTarget: true,
                    supportDatabaseUnion: true,
                    supportExcelView: true,
                    supportGeneralControl: true,
                    supportIncrementUpdate: true,
                    supportMobileClient: true,
                    supportMultiStatisticsWidget: true,
                    supportOLAPTable: true,
                    supportReportShare: true,
                    supportSimpleControl: true
                },
                reportName: "axxx",		//模板名称
                rootURL: "",			//
                sessionID: 21550,		//sessionID
                userId: "-999"		//当前用户ID
            }
        }
    },

    _defaultConfig: function () {
        return BI.extend(DataStructureModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        DataStructureModel.superclass._init.apply(this, arguments);
    }
});