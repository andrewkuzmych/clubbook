//
//  ClubbookCommunicatorDelegate.h
//  Clubbook
//
//  Created by Andrew on 6/19/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol ClubbookCommunicatorDelegate <NSObject>

- (void)fbLoginJSON:(NSData *)objectNotation;
- (void)signupJSON:(NSData *)objectNotation;
- (void)chatJSON:(NSData *)objectNotation;
- (void)signinJSON:(NSData *)objectNotation;
- (void)receivedPlacesJSON:(NSData *)objectNotation;
- (void)receivedPlaceJSON:(NSData *)objectNotation;
- (void)receivedUserJSON:(NSData *)objectNotation;
- (void)receivedFriendsJSON:(NSData *)objectNotation;
- (void)receivedPendingFriendsJSON:(NSData *)objectNotation;
- (void)receivedFriendJSON:(NSData *)objectNotation;
- (void)sendFriendJSON:(NSData *)objectNotation;
- (void)confirmFriendJSON:(NSData *)objectNotation;
- (void)removeFriendJSON:(NSData *)objectNotation;
- (void)removeFriendRequestJSON:(NSData *)objectNotation;
- (void)receivedConversationJSON:(NSData *)objectNotation;
- (void)receivedConversationsJSON:(NSData *)objectNotation;
- (void)unreadMessagesJSON:(NSData *)objectNotation;
- (void)readChatJSON:(NSData *)objectNotation;
- (void)checkinJSON:(NSData *)objectNotation userInfo:(NSObject *) userInfo;
- (void)checkoutJSON:(NSData *)objectNotation userInfo:(NSObject *) userInfo;
- (void)updateCheckinJSON:(NSData *)objectNotation;
- (void)addUserImageJSON:(NSData *)objectNotation;
- (void)updateUserImageJSON:(NSData *)objectNotation;
- (void)deleteUserImageJSON:(NSData *)objectNotation;
- (void)updateUserJSON:(NSData *)objectNotation;
- (void)deleteUserJSON:(NSData *)objectNotation;
- (void)changeUserPushJSON:(NSData *)objectNotation;
- (void)getConfigJSON:(NSData *)objectNotation;

- (void)failedWithError:(NSError *)error;


@end
