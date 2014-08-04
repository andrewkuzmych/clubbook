//
//  AddFriendButton.h
//  Clubbook
//
//  Created by Andrew on 7/31/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AddFriendButton : UIButton
{
@private
    NSMutableDictionary *backgroundStates;
    UIColor * highlightedColor;
    UIColor * normalColor;
    
@public
}

@property (weak, nonatomic) IBOutlet NSString *friendState;

- (void) setButtonState:(NSString *) state;
/*- (void) setAddFriendState:(NSString*) text;
- (void) setConfirmFriendState:(NSString*) text;
- (void) setUnFriendState:(NSString*) text;
- (void) setSentFriendRequestState:(NSString*) text;*/
@end
