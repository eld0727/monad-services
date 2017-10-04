package go.to.monad

package object domain {

  case class UserInfo(id: String,
                      vkInfo: VkInfo,
                      twitterInfo: TwitterInfo)

  case class VkInfo(userId: String,
                    accessToken: String,
                    lastNews: Option[Long])

  case class TwitterInfo(userId: String,
                         accessToken: String,
                         lastTweet: Option[Long])

  case class News(id: Long,
                  text: String)
}
