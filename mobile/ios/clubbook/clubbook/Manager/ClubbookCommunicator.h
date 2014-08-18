//
//  ClubbookCommunicator.h
//  Clubbook
//
//  Created by Andrew on 6/19/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

#define baseURL @"http://clubbookapp.herokuapp.com/_s/"
//#define baseURL @"http://192.168.2.113:3000/_s/"


@protocol ClubbookCommunicatorDelegate;

@interface ClubbookCommunicator : NSObject

@property (weak, nonatomic) id<ClubbookCommunicatorDelegate> delegate;

- (void)fbLoginUser:(NSString *) fbId fbAccessToken:(NSString *) fbAccessToken fbAccessExpires:(NSString *) fbAccessExpires gender:(NSString *) gender name:(NSString *) name email:(NSString *) email avatar:(NSString *) avatar bio:(NSString *) bio country:(NSString *) country dob:(NSString *) dob;

- (void)signupUser:(NSString *) name email:(NSString *) email gender:(NSString *) gender city:(NSString *) city pass:(NSString *) pass dob:(NSString *) dob avatar:(NSString *) avatar country:(NSString *) country bio:(NSString *) bio;

- (void)signinUser:(NSString *) email pass:(NSString *) pass;

- (void)chat:(NSString *) user_from user_to:(NSString *) user_to msg:(NSString *) msg msg_type:(NSString *) msg_type accessToken:(NSString *) accessToken;

- (void)retrieveConversation:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken;

- (void)retrieveConversations:(NSString *) userId accessToken:(NSString *) accessToken;

- (void)unreadMessages:(NSString *) accessToken;

- (void)readChat:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken;

- (void)retrievePlaces:(double) distance lat:(double) lat lon:(double) lon accessToken:(NSString *) accessToken;

- (void)retrievePlace:(NSString *) clubId accessToken:(NSString *) accessToken;

- (void)retrieveUser:(NSString *) accessToken;

- (void)retrieveFriend:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)retrieveFriends:(NSString *) userId accessToken:(NSString *) accessToken;

- (void)retrievePendingFriends:(NSString *) userId accessToken:(NSString *) accessToken;

- (void)checkin:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo;

- (void)checkout:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo;

- (void)updateCheckin:(NSString *) clubId accessToken:(NSString *) accessToken;

- (void)addUserImage:(NSString *) userId avatar:(NSString *) avatar accessToken:(NSString *) accessToken;

- (void)updateUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken;

- (void)deleteUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken;

- (void)updateUser:(NSString *) accessToken name:(NSString *) name gender:(NSString *) gender  dob:(NSString *) dob country:(NSString *) country bio:(NSString *) bio;

- (void)deleteUser:(NSString *) accessToken;

- (void)changeUserPush:(NSString *) accessToken push:(BOOL) push;

- (void)sendFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)confirmFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)removeFriend:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)removeFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)getConfig;


@end
