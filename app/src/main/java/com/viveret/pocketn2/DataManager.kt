package com.viveret.pocketn2

import android.content.Context
import com.viveret.pocketn2.view.fragments.data.fmt.*
import com.viveret.tinydnn.basis.*
import com.viveret.tinydnn.data.DataLifecycleListener
import com.viveret.tinydnn.data.DataMethod
import com.viveret.tinydnn.data.DataValues
import com.viveret.tinydnn.data.Scenario
import com.viveret.tinydnn.data.challenge.ChallengeMetaInfo
import com.viveret.tinydnn.data.challenge.easy.Cifar10Challenge
import com.viveret.tinydnn.data.challenge.easy.MnistChallenge
import com.viveret.tinydnn.data.knowledge.Cifar10Knowledge
import com.viveret.tinydnn.data.knowledge.MNISTKnowledge
import com.viveret.tinydnn.data.knowledge.MNISTKnowledge2
import com.viveret.tinydnn.data.repo.JsonRepo
import com.viveret.tinydnn.data.scenario.binaryfile.CanvasScenario
import com.viveret.tinydnn.data.scenario.binaryfile.Cifar10Scenario
import com.viveret.tinydnn.data.scenario.binarygate.BinaryGateScenario
import com.viveret.tinydnn.data.scenario.binarygate.ManualBinaryTrainScenario
import com.viveret.tinydnn.data.scenario.catchall.CameraScenario
import com.viveret.tinydnn.data.scenario.catchall.SensorTrain
import com.viveret.tinydnn.data.scenario.textfile.ReviewSentimentScenario
import com.viveret.tinydnn.data.scenario.textfile.TextFileScenario
import com.viveret.tinydnn.data.scenario.textfile.TextSentimentScenario
import com.viveret.tinydnn.data.train.DataSliceReader
import com.viveret.tinydnn.data.transform.MnistPackageGenerator
import com.viveret.tinydnn.project.NeuralNetProject
import com.viveret.tinydnn.project.ProjectProvider
import com.viveret.tinydnn.scenario.binaryfile.mnist.MNISTImportScenario
import com.viveret.tinydnn.scenario.binaryfile.mnist.MNISTOnlineScenario
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DataManager {
//    val cmuIgnore = HostedStreamPackage("a304354a-4da5-11e9-8646-d663bd873d92","a304354a-4da5-11e9-8646-d663bd873d92",
//            "CMU Movie Summary Corpus", "dataset of movie plot summaries and associated metadata", "a30433b0-4da5-11e9-8646-d663bd873d93", true,
//            "blob/master/MovieSummaries/movies11.tsv?raw=true", "a30436f8-4da5-11e9-8646-d663bd873d93",
//            null, null,
//            "blob/master/MovieSummaries/movies11.tsv?raw=true", "a30436f8-4da5-11e9-8646-d663bd873d93",
//            null, null)

    val context: Context
    private val cacheSizeKb: Int
    private val cachedSuiteIds = ArrayList<UUID>()
    private val scenarios = listOf(MNISTOnlineScenario(), MNISTImportScenario(),
            CameraScenario(), TextFileScenario(), SensorTrain(),
            BinaryGateScenario("XOR", listOf(0, 1, 1, 0), UUID.fromString("539dd1b0-4151-11e9-b210-d663bd873d93")),
            BinaryGateScenario("AND", listOf(0, 0, 0, 1), UUID.fromString("539dd32c-4151-11e9-b210-d663bd873d93")),
            BinaryGateScenario("OR", listOf(0, 1, 1, 1), UUID.fromString("58f7a34c-4152-11e9-b210-d663bd873d93")),
            ManualBinaryTrainScenario(), CanvasScenario(), ReviewSentimentScenario(), TextSentimentScenario(), Cifar10Scenario())

    val challenges = listOf(MnistChallenge(), Cifar10Challenge()).map { x -> x.id to x }.toMap()

    val knowledgeCatalog
            get() = listOf(Cifar10Knowledge(context), MNISTKnowledge(context), MNISTKnowledge2(context)).map { x -> x.stream.id to x }.toMap()

    val generators
            get() = listOf(MnistPackageGenerator(context))
    /*
    , MovieSummaryCategoryScenario(), CMUMovieSummaryScenario(),
            , ,
     */

    val DATA_METHOD_IDS = DataMethod.values().map { k -> Pair(k.stringResId, k) }.toMap()

    private val memoryCachedFiles = HashMap<UUID, File>()
    private val memoryCachedData = HashMap<UUID, DataSlice>()
    private val memoryCachedTrainingData = HashMap<UUID, DataValues>()
    private val memoryCachedVectLists = HashMap<UUID, ArrayList<Vect>>()
    private val memoryCachedLabelLists = HashMap<UUID, ArrayList<Long>>()
    private val memoryCachedProjects = HashMap<UUID, NeuralNetProject>()
    private val memoryCachedDataValueStream = HashMap<UUID, DataSliceReader>()
    private val listeners = ArrayList<DataLifecycleListener>()

    private constructor(context: Context) {
        instances[context] = this
        this.context = context
        if (context is ProjectProvider) {
            this.scenarios.forEach {x -> x.init(context.project!!, context) }
        }
        this.cacheSizeKb = context.resources.getInteger(R.integer.cacheSizeKb)

        HostedStreamPackage.addRepo(JsonRepo(this.context, InputStreamReader(context.resources.openRawResource(R.raw.repo)).readText()))
    }

    fun cacheFileToMemory(fileId: UUID, file: File) {
        memoryCachedFiles[fileId] = file
    }

    fun getCachedFile(fileId: UUID): File {
        return this.memoryCachedFiles[fileId]!!
    }

    fun getDataSuiteFile(suiteId: UUID, fileId: UUID): File {
        val suiteDir = File(context.cacheDir, suiteId.toString())
        if ((suiteDir.exists() && suiteDir.isDirectory) || suiteDir.mkdirs()) {
            return File(suiteDir, fileId.toString())
        } else {
            throw Exception("Could not get host directory")
        }
    }

    fun getScenariosForDataMethod(dataMethod: DataMethod): List<Scenario> =
            this.scenarios.filter { s -> s.dataMethod == dataMethod }.toList()

    fun getScenario(id: UUID): Scenario = this.scenarios.single { x -> x.id == id }

    fun getDataFile(fileId: UUID): File =
            this.memoryCachedFiles[fileId]
                    ?: throw IllegalArgumentException("Data Vect List $fileId not found")

    fun putFile(file: File): UUID {
        val id = UUID.randomUUID()
        this.memoryCachedFiles[id] = file
        return id
    }

    fun putData(outputData: DataSlice): UUID {
        val id = UUID.randomUUID()
        this.memoryCachedData[id] = outputData
        return id
    }

    fun getData(dataId: UUID): DataSlice = this.memoryCachedData.remove(dataId)
            ?: throw IllegalArgumentException("Data $dataId not found")

    fun putDataVectList(data: ArrayList<Vect>): UUID {
        val id = UUID.randomUUID()
        this.memoryCachedVectLists[id] = data
        return id
    }

    fun getDataVectList(id: UUID): ArrayList<Vect> = this.memoryCachedVectLists.remove(id)
            ?: throw IllegalArgumentException("Data Vect List $id not found")

    fun putDataLabelList(data: ArrayList<Long>): UUID {
        val id = UUID.randomUUID()
        this.memoryCachedLabelLists[id] = data
        return id
    }

    fun getDataLabelList(id: UUID): ArrayList<Long> = this.memoryCachedLabelLists.remove(id)
            ?: throw IllegalArgumentException("Data Label List $id not found")

    fun getDefaultViewFor(data: DataValues): DataFormatView {
        return if (data[DataRole.Input]?.valueMetaInfo != null) {
            this.getViewForMime(data[DataRole.Input]!!.valueMetaInfo!!.mime)
        } else {
            ToStringView()
        }
    }

    fun deleteSuite(dataSuiteId: UUID) {
        this.cachedSuiteIds.remove(dataSuiteId)
        val pkg = HostedStreamPackage.fromId(dataSuiteId)
        pkg.delete(DataSource.LocalFile)
        for (listener in this.listeners) {
            listener.onFreed(pkg)
        }
    }

    private fun detachFileIds(fileIds: List<UUID>) {
        for (x in arrayOf(this.memoryCachedData, this.memoryCachedFiles, this.memoryCachedLabelLists, this.memoryCachedVectLists)) {
            for (id in fileIds) {
                x.remove(id)
            }
        }
    }

    fun backgroundColors(): IntArray =
            arrayOf(R.color.colorSecondary, R.color.colorSecondaryDark).map { x -> context.resources.getColor(x) }.toIntArray()

    fun supportedDataMethods(project: NeuralNetProject): Array<DataMethod> {
        val compatScenarios = this.scenarios.filter { s -> s.compatibleWithNetwork(project) }.toHashSet()
        return this.DATA_METHOD_IDS.values.filter { x -> project.supportsDataMethod(x) && compatScenarios.any { s -> s.dataMethod == x } }.toTypedArray()
    }

    fun getPoject(projectId: UUID): NeuralNetProject = memoryCachedProjects[projectId]!!

    fun putProject(project: NeuralNetProject) {
        this.memoryCachedProjects[project.id] = project
    }

    fun addListener(dataLifecycleListener: DataLifecycleListener) {
        this.listeners.add(dataLifecycleListener)
    }

    fun removeListener(dataLifecycleListener: DataLifecycleListener) {
        this.listeners.remove(dataLifecycleListener)
    }

    fun validateStreamPackageCached(streamPackage: StreamPackage) {
        if (!this.cachedSuiteIds.contains(streamPackage.id)) {
            this.cachedSuiteIds.add(streamPackage.id)
        }
    }

    fun getChallengeMetaInfo(id: UUID): ChallengeMetaInfo = this.challenges.getValue(id)

    fun getViewForMime(mime: String): DataFormatView {
        return when (mime) {
            "application/octet-stream" -> BinaryGateView()
            "application/cifar10" -> CifarImageView()
            else -> IdxImageView()
        }
    }

    fun putTrainingData(trainingData: DataValues): UUID {
            val id = UUID.randomUUID()
            this.memoryCachedTrainingData[id] = trainingData
            return id
    }

    fun getTrainingData(dataId: UUID): DataValues = this.memoryCachedTrainingData.remove(dataId)
            ?: throw IllegalArgumentException("Data $dataId not found")

    fun putDataValueStream(data: DataSliceReader): UUID {
        val id = UUID.randomUUID()
        this.memoryCachedDataValueStream[id] = data
        return id
    }

    fun getDataValueStream(id: UUID): DataSliceReader = this.memoryCachedDataValueStream[id]!!

    companion object {
        private val instances = HashMap<Context, DataManager>()

        fun get(context: Context): DataManager {
            return if (instances.isNotEmpty()) {
                instances.values.first()
            } else {
                val ret = DataManager(context)
                instances[context] = ret
                ret
            }
        }
    }
}