//
//  BonusClubManager.m
//  BrowseMeetup
//
//  Created by Andrew on 10/2/13.
//  Copyright (c) 2013 TAMIM Ziad. All rights reserved.
//

#import "ClubbookManager.h"

#import "ObjectBuilder.h"
#import "ClubbookCommunicator.h"
#import "Chat.h"
#import "ErrorViewController.h"
#import "UnreadMessages.h"
#import "Config.h"


@implementation ClubbookManager


- (void)fbLoginUser:(NSString *) fbId fbAccessToken:(NSString *) fbAccessToken fbAccessExpires:(NSString *) fbAccessExpires gender:(NSString *) gender name:(NSString *) name email:(NSString *) email avatar:(NSString *) avatar bio:(NSString *) bio country:(NSString *) country dob:(NSString *) dob
{
    [self.communicator fbLoginUser:fbId fbAccessToken:fbAccessToken fbAccessExpires:fbAccessExpires gender:gender name:name email:email avatar:avatar bio:bio country:country dob:dob];
}

- (void)signupUser:(NSString *) name email:(NSString *) email gender:(NSString *) gender city:(NSString *) city pass:(NSString *) pass dob:(NSString *) dob avatar:(NSString *) avatar country:(NSString *) country bio:(NSString *) bio;
{
    [self.communicator signupUser:name email:email gender:gender city:city pass:pass dob:dob avatar:avatar country:country bio:bio];
}

- (void)signinUser:(NSString *) email pass:(NSString *) pass
{
     [self.communicator signinUser:email pass:pass];
}

- (void)chat:(NSString *) user_from user_to:(NSString *) user_to msg:(NSString *) msg msg_type:(NSString *) msg_type accessToken:(NSString *) accessToken
{
    [self.communicator chat:user_from user_to:user_to msg:msg msg_type:msg_type accessToken:accessToken];
}

- (void)retrievePlaces:(double) lat lon:(double) lon accessToken:(NSString *) accessToken
{
    [self.communicator retrievePlaces:lat lon:lon accessToken:accessToken];
}

- (void)retrievePlace:(NSString *) clubId accessToken:(NSString *) accessToken
{
    [self.communicator retrievePlace:clubId accessToken:accessToken];
}

- (void)retrieveUser:(NSString *) accessToken
{
    [self.communicator retrieveUser:accessToken];
}

- (void)retrieveFriend:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator retrieveFriend:friendId accessToken:accessToken];
}

- (void)retrieveConversation:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken
{
    [self.communicator retrieveConversation:fromUser toUser:toUser accessToken:accessToken];
}

- (void)retrieveConversations:(NSString *) userId accessToken:(NSString *) accessToken
{
    [self.communicator retrieveConversations:userId accessToken:accessToken];
}

- (void)checkin:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo
{
    [self.communicator checkin:clubId accessToken:accessToken userInfo:userInfo];
}

- (void)checkout:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo
{
    [self.communicator checkout:clubId accessToken:accessToken userInfo:userInfo];
}

- (void)updateCheckin:(NSString *) clubId accessToken:(NSString *) accessToken
{
    [self.communicator updateCheckin:clubId accessToken:accessToken];
}

- (void) readChat:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken
{
    [self.communicator readChat:fromUser toUser:toUser accessToken: accessToken];
}

- (void)unreadMessages:(NSString *) accessToken
{
    [self.communicator unreadMessages:accessToken];
}

- (void)addUserImage:(NSString *) userId avatar:(NSString *) avatar accessToken:(NSString *) accessToken
{
    [self.communicator addUserImage:userId avatar:avatar accessToken:accessToken];
}

- (void)changeUserPush:(NSString *) accessToken push:(BOOL) push
{
    [self.communicator changeUserPush:accessToken push:push];
}

- (void)updateUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken
{
    [self.communicator updateUserImage:userId objectId:objectId accessToken:accessToken];
}

- (void)deleteUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken
{
     [self.communicator deleteUserImage:userId objectId:objectId accessToken:accessToken];
}

- (void)updateUser:(NSString *) accessToken name:(NSString *) name gender:(NSString *) gender  dob:(NSString *) dob country:(NSString *) country bio:(NSString *) bio
{
    [self.communicator updateUser:accessToken name:name gender:gender dob:dob country:country bio:bio];
}

- (void)deleteUser:(NSString *) accessToken
{
    [self.communicator deleteUser:accessToken];
}

- (void)sendFriendReguest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator sendFriendRequest:userId friendId:friendId accessToken:accessToken];
}

- (void)confirmFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator confirmFriendRequest:userId friendId:friendId accessToken:accessToken];
}

- (void)removeFriend:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator removeFriend:userId friendId:friendId accessToken:accessToken];
}

- (void)removeFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator removeFriendRequest:userId friendId:friendId accessToken:accessToken];
}

- (void)retrieveFriends:(NSString *) userId accessToken:(NSString *) accessToken
{
    [self.communicator retrieveFriends:userId accessToken:accessToken];
}

- (void)retrievePendingFriends:(NSString *) userId accessToken:(NSString *) accessToken
{
    [self.communicator retrievePendingFriends:userId accessToken:accessToken];
}

- (void)getConfig
{
    [self.communicator getConfig];
}

- (void)getConfigJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    Config *config = [ObjectBuilder getConfigFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didGetConfig:config];
    }
}

- (void)receivedFriendsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *friends = [ObjectBuilder friendsJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRetrieveFriends:friends];
    }
}

- (void)changeUserPushJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didChangePush:user];
    }
}

- (void)receivedPendingFriendsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *friends = [ObjectBuilder friendsJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRetrievePendingFriends:friends];
    }
}


- (void)sendFriendJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder tempUserFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didSendFriend:user];
    }

}

- (void)confirmFriendJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder tempUserFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didConfirmFriend:user];
    }
}

- (void)removeFriendJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder tempUserFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRemoveFriend:user];
    }
}

- (void)removeFriendRequestJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder tempUserFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRemoveFriendRequest:user];
    }
}

- (void)updateUserJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUpdateUser:user];
    }
}

- (void)deleteUserJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    //User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didDeleteUser:@"ok"];
    }
}

- (void)deleteUserImageJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didDeleteUserImage:user];
    }
}

- (void)updateUserImageJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUpdateUserImage:user];
    }
}

- (void)addUserImageJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didAddUserImage:user];
    }
}

- (void)readChatJSON:(NSData *)objectNotation
{
    
}

- (void)updateCheckinJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder checkinFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUpdateCheckin:user];
    }
}

- (void)unreadMessagesJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    UnreadMessages *unreadMessages = [ObjectBuilder unreadMessagesFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUnreadMessages:unreadMessages];
    }
}

- (void)checkinJSON:(NSData *)objectNotation userInfo:(NSObject *) userInfo
{
    NSError *error = nil;
    User *user = [ObjectBuilder checkinFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
    } else {
        [self.delegate didCheckin:user userInfo:userInfo];
    }
}

- (void)checkoutJSON:(NSData *)objectNotation userInfo:(NSObject *) userInfo
{
    NSError *error = nil;
    User *user = [ObjectBuilder checkinFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
    } else {
        [self.delegate didCheckout:user userInfo:userInfo];
    }
}

- (void)receivedPlacesJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *places = [ObjectBuilder placesFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceivePlaces:places];
    }
}

- (void)receivedConversationJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    Chat *chat = [ObjectBuilder chatFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveConversation:chat];
    }
}

- (void)receivedConversationsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *chats = [ObjectBuilder conversationsJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveConversations:chats];
    }
}


- (void)receivedUserJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveUser:user];
    }
}

- (void)receivedFriendJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveFriend:user];
    }
}

- (void)receivedPlaceJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    Place *place = [ObjectBuilder placeFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceivePlace:place];
    }
}

- (void)signupJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder signupFromJSON:objectNotation error:&error];

    if (error != nil) {
        [self.delegate  failedWithError:error];

    } else {
        [self.delegate didSignapUser:user];
    }
}

- (void)chatJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    //TODO: parse resondse
    //User *user = [ObjectBuilder signupFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didChat:@"ok"];
    }
}

- (void)signinJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder signinFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didSigninUser:user];
    }
}

- (void)fbLoginJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder fbLoginFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didFbLoginUser:user];
    }
}

- (void)failedWithError:(NSError *)error
{
    [self.delegate failedWithError:error];
}


@end
