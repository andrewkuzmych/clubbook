//
//  FloatingButton.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FloatingButton : UIButton

{
@private
    NSMutableDictionary *backgroundStates;
    UIColor * highlightedColor;
    UIColor * normalColor;
    
@public
}

@property(nonatomic) BOOL statusOn;

- (void) setMainState:(NSString*) text;
- (void) setSecondState:(NSString*) text;

- (UIColor*) backgroundColorForState:(UIControlState) _state;@end
